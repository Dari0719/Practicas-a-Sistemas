package co.edu.poli.allten.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Busca expresiones con + − × ÷ y paréntesis que den un objetivo
 * a partir de los números del reto.
 */
public final class ExpressionSolver {

	/** true: cada expresión usa los cuatro números, una vez cada uno. false: puede usar solo algunos. */
	public static final boolean USE_ALL_NUMBERS = true;

	private ExpressionSolver() {
	}

	public static List<String> solve(int[] numbers, int target, int limit) {
		int n = numbers.length;
		int full = (1 << n) - 1;

		// table.get(mask): expresiones distintas que usan justo los números de ese subconjunto
		List<List<Expr>> table = new ArrayList<>(Collections.nCopies(full + 1, null));
		for (int mask = 1; mask <= full; mask++) {
			Map<String, Expr> found = new LinkedHashMap<>();
			if (Integer.bitCount(mask) == 1) {
				Expr e = Expr.number(numbers[Integer.numberOfTrailingZeros(mask)]);
				found.put(e.text, e);
			} else {
				for (int sub = (mask - 1) & mask; sub > 0; sub = (sub - 1) & mask) {
					int rest = mask ^ sub;
					if (sub < rest) {
						continue; // cada pareja de subconjuntos una sola vez
					}
					for (Expr a : table.get(sub)) {
						for (Expr b : table.get(rest)) {
							for (Expr e : Expr.combine(a, b)) {
								found.putIfAbsent(e.text, e);
							}
						}
					}
				}
			}
			table.set(mask, new ArrayList<>(found.values()));
		}

		Set<String> result = new LinkedHashSet<>();
		for (int mask = 1; mask <= full; mask++) {
			if (USE_ALL_NUMBERS && mask != full) {
				continue;
			}
			for (Expr e : table.get(mask)) {
				if (e.value.equalsInt(target)) {
					result.add(e.text);
				}
			}
		}

		// Primero las más simples: sin paréntesis, sin divisiones y más cortas
		List<String> sorted = new ArrayList<>(result);
		sorted.sort(Comparator.<String>comparingInt(ExpressionSolver::complexity)
				.thenComparing(Comparator.<String>naturalOrder()));
		return sorted.size() > limit ? new ArrayList<>(sorted.subList(0, limit)) : sorted;
	}

	private static int complexity(String text) {
		int parentheses = 0;
		for (char c : text.toCharArray()) {
			if (c == '(') {
				parentheses++;
			}
		}
		return parentheses * 100 + (text.contains("÷") ? 50 : 0) + text.length();
	}

	/** Número racional exacto, para que las divisiones no pierdan precisión. */
	private record Fraction(long num, long den) {

		static Fraction of(long n, long d) {
			if (d < 0) {
				n = -n;
				d = -d;
			}
			long g = gcd(Math.abs(n), d);
			return new Fraction(n / g, d / g);
		}

		private static long gcd(long a, long b) {
			while (b != 0) {
				long t = a % b;
				a = b;
				b = t;
			}
			return a == 0 ? 1 : a;
		}

		Fraction add(Fraction o) { return of(num * o.den + o.num * den, den * o.den); }
		Fraction sub(Fraction o) { return of(num * o.den - o.num * den, den * o.den); }
		Fraction mul(Fraction o) { return of(num * o.num, den * o.den); }
		Fraction div(Fraction o) { return of(num * o.den, den * o.num); }
		boolean isZero()         { return num == 0; }
		boolean equalsInt(int t) { return den == 1 && num == t; }
	}

	/**
	 * Expresión en forma canónica: una suma (sumandos y restandos) o un producto
	 * (factores y divisores) ya aplanados y ordenados. Así 1 + 2 + 3 y 3 + 2 + 1
	 * se escriben igual y no aparecen repetidas.
	 */
	private static final class Expr {
		enum Kind { NUM, SUM, PROD }

		final Kind kind;
		final Fraction value;
		final List<Expr> plus;   // SUM: sumandos | PROD: factores
		final List<Expr> minus;  // SUM: restandos | PROD: divisores
		final String text;

		private Expr(Kind kind, Fraction value, List<Expr> plus, List<Expr> minus, String text) {
			this.kind = kind;
			this.value = value;
			this.plus = plus;
			this.minus = minus;
			this.text = text;
		}

		static Expr number(int v) {
			return new Expr(Kind.NUM, Fraction.of(v, 1), List.of(), List.of(), Integer.toString(v));
		}

		static List<Expr> combine(Expr a, Expr b) {
			List<Expr> out = new ArrayList<>(6);
			out.add(add(a, b));
			out.add(sub(a, b));
			out.add(sub(b, a));
			out.add(mul(a, b));
			if (!b.value.isZero()) {
				out.add(div(a, b));
			}
			if (!a.value.isZero()) {
				out.add(div(b, a));
			}
			return out;
		}

		private List<Expr> plusTerms()  { return kind == Kind.SUM ? plus : List.of(this); }
		private List<Expr> minusTerms() { return kind == Kind.SUM ? minus : List.of(); }
		private List<Expr> mulTerms()   { return kind == Kind.PROD ? plus : List.of(this); }
		private List<Expr> divTerms()   { return kind == Kind.PROD ? minus : List.of(); }

		private static Expr add(Expr a, Expr b) {
			return sum(a.value.add(b.value),
					concat(a.plusTerms(), b.plusTerms()),
					concat(a.minusTerms(), b.minusTerms()));
		}

		private static Expr sub(Expr a, Expr b) {
			return sum(a.value.sub(b.value),
					concat(a.plusTerms(), b.minusTerms()),
					concat(a.minusTerms(), b.plusTerms()));
		}

		private static Expr mul(Expr a, Expr b) {
			return prod(a.value.mul(b.value),
					concat(a.mulTerms(), b.mulTerms()),
					concat(a.divTerms(), b.divTerms()));
		}

		private static Expr div(Expr a, Expr b) {
			return prod(a.value.div(b.value),
					concat(a.mulTerms(), b.divTerms()),
					concat(a.divTerms(), b.mulTerms()));
		}

		private static Expr sum(Fraction value, List<Expr> plus, List<Expr> minus) {
			String t = joinSorted(plus, " + ", false);
			if (!minus.isEmpty()) {
				t += " − " + joinSorted(minus, " − ", false);
			}
			return new Expr(Kind.SUM, value, plus, minus, t);
		}

		private static Expr prod(Fraction value, List<Expr> factors, List<Expr> divisors) {
			String t = joinSorted(factors, " × ", true);
			if (!divisors.isEmpty()) {
				t += " ÷ " + joinSorted(divisors, " ÷ ", true);
			}
			return new Expr(Kind.PROD, value, factors, divisors, t);
		}

		private static String joinSorted(List<Expr> items, String separator, boolean wrapSums) {
			List<String> texts = new ArrayList<>();
			for (Expr e : items) {
				texts.add(wrapSums && e.kind == Kind.SUM ? "(" + e.text + ")" : e.text);
			}
			Collections.sort(texts);
			return String.join(separator, texts);
		}

		private static List<Expr> concat(List<Expr> x, List<Expr> y) {
			List<Expr> out = new ArrayList<>(x.size() + y.size());
			out.addAll(x);
			out.addAll(y);
			return out;
		}
	}
}
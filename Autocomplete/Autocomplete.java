import java.util.*;
import java.io.*;

public class Autocomplete {
    // Comparator for prefix-finding that is dependent on Collections.binarySearch implementation
    private static final Comparator<Term> BY_PREFIX = (term, prefix) -> {
        int len = Math.min(term.query().length(), prefix.query().length());
        return term.query().substring(0, len).compareToIgnoreCase(prefix.query());
    };
    // Maximum number of matches to print
    private static final int MAX_MATCHES = 10;
    // Corpus of terms sorted in lexicographic order by query
    private List<Term> terms;

    public Autocomplete(String filename) throws FileNotFoundException {
        terms = new ArrayList<>();
        try (Scanner input = new Scanner(new File(filename))) {
            while (input.hasNextLine()) {
                String[] parts = input.nextLine().split("\t");
                int weight = Integer.parseInt(parts[0]);
                String query = parts[1];
                terms.add(new Term(query, weight));
            }
        }
        Collections.sort(terms);
    }

    // Returns all terms that start with the given prefix, in descending order of weight.
    public List<Term> allMatches(String prefix) {
        return allMatches(new Term(prefix, 0));
    }

    // Returns all terms that start with the given prefix, in descending order of weight.
    public List<Term> allMatches(Term prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix cannot be null");
        }
        if (prefix.query().length() == 0) {
            return List.of();
        }
        int index = Collections.binarySearch(terms, prefix, BY_PREFIX);
        if (index < 0) {
            return List.of();
        }
        List<Term> matches = new ArrayList<>(terms.subList(
            lower(terms.subList(0, index + 1), prefix),
            upper(terms.subList(index, terms.size()), prefix, index) + 1
            ));
        Collections.sort(matches, Term::compareToByReverseWeight);
        return matches;
    }

    private static int lower(List<Term> problem, Term prefix) {
        int index = Collections.binarySearch(problem, prefix, BY_PREFIX);
        if (index < 0) {
            return index;
        }
        int next = lower(problem.subList(0, index), prefix);
        if (next < 0) {
            return index;
        } else {
            return next;
        }
    }

    private static int upper(List<Term> problem, Term prefix, int offset) {
        int index = Collections.binarySearch(problem, prefix, BY_PREFIX);
        if (index < 0) {
            return index;
        }
        int next = upper(problem.subList(index + 1, problem.size()), prefix, offset + index + 1);
        if (next < 0) {
            return offset + index;
        } else {
            return next;
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length != 1) {
            throw new IllegalArgumentException("java Autocomplete [tsv file]");
        }
        String filename = args[0];
        Autocomplete autocomplete = new Autocomplete(filename);
        try (Scanner stdin = new Scanner(System.in)) {
            System.out.print("Query: ");
            while (stdin.hasNextLine()) {
                String prefix = stdin.nextLine();
                if (prefix.length() == 0) {
                    System.exit(0);
                }
                List<Term> matches = autocomplete.allMatches(new Term(prefix, 0));
                System.out.println(matches.size() + " matches");
                for (int i = 0; i < Math.min(matches.size(), MAX_MATCHES); i++) {
                    System.out.println(matches.get(i));
                }
                System.out.print("Query: ");
            }
        }
    }
}

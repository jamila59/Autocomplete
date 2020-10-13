// Jamila Aliyeva
// CSE 143 DE Sahil Unadkat
// Assessment 1: Autocomplete 
// A query term and its weight
public class Term implements Comparable<Term>{

    private String query;
    private int weight;

    /**
        *The constructor sets field of query and weight into early state 
        * @ param query - term to be assigned
        * @ param weight - the weight of the term 
        * @ throws - NullPointerException if word is null 
        * @ throws - IllegalArgumentException if weight is negative
    */
   public Term(String query, int weight)
   {
       if(query == null)
       {
           throw new NullPointerException("Query cannot be null");
       }
       if(weight < 0)
       {
           throw new IllegalArgumentException("weight cannot be less than zero");
       }
       this.query = query;
       this.weight = weight;
   }

    //The getter method for the accessibility outside the Term class
    //return the query of object
        
   public String query()
   {
       return query;
   }
    //The getter method for the accessibility outside the Term class
    //return the weight of object
   public int weight()
   {
       return weight;
   }

   
    //Compares to another term in descending order by weight.
    //param another Term object
    //returns value 0 if (x == y)
    //returns value less than zero if (x < y)
    //return value greater than zero if (x > y)
   public int compareToByReverseWeight(Term other)
   {
       return Integer.compare(other.weight, this.weight());
   }

    
    //Compares to another term in lexicographic order by query and ignoring case and returns query that comes first.
    //param- another Term object
   public int compareTo(Term other)
   {
       return this.query.compareToIgnoreCase(other.query());
   }
    
    //Returns the terms query.

   public String toString()
   {
       return  query;
   }

    public static void main(String[] args) {
        Term t1 = new Term("hello world", 0);
        Term t2 = new Term("hi", 1);
        int cmp = t1.compareTo(t2);
        // System.out.print(t1.compareToByReverseWeight(t2));
        System.out.println(cmp);
    }
}

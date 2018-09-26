package spamclassifier;

import java.util.regex.Pattern;

public class TextCleaner {
    
/// Section copes with cleaning an input string, removing punctuation, numerals inside strings etc. and finally returns string to lower case     
    
    /// Removes everything from input string which is non-alphabetic
    private static String returnCharOnly(String str){
        char[] chArr = str.toCharArray();
        String ret = "";
        for (char ch : chArr){
            if (Character.isAlphabetic(ch)){ // If character is alphabetic, add it to return value
                ret = ret + ch;
            }
        }
        return ret;
    }
    
    private String cleanText(String str){
        if (str != null && str.length() >= 4){ // Only give strings of sice >= 4 characters the chance to be added to hash table
            str = returnCharOnly(str);
            if (!str.isEmpty() && ! str.equals("") && str.length() >= 4)
                return str.toLowerCase();
        }
        return null;
    }
    
/// Special String-Cases section. This section determines, whether a token is a special instance of a string, e.g. E-Mail-Address, Internet-Address, number etc.
    
    /// This method checks whether a token is a date. It uses java regex expressions. It classifies tokens of the following patterns as dates:
    /// d.m.yy || dd.mm.yy || d.mm.yyyy || d-m-yy || d/m/yy and all logical combinations of those patterns, which may be followed by full stop or comma.
    public boolean isDate(String date) {
            
        Pattern DATE_PATTERN = Pattern.compile("^([1-9]|0[1-9]|1[0-9]|2[0-9]|3[0-1])(\\.|\\/|\\-)((0[1-9])|([1-9]|1[0-2]))(\\.|\\/|\\-)(\\d{2}|\\d{4})(\\.|\\,|$)");

            if (DATE_PATTERN.matcher(date).matches()) 
                return true;
            
            return false;
    }
    
    /// This method checks whether a String is numeric (return value true) or not (return value false)
    private boolean isNumeric(String s) {
        if (s != null){ /// If string equals null, return false
            char[] parts = s.toCharArray();
            /// Using Java regex expression, first define two possible patterbs how a regular number would look like: digits || digits + '.' + digits
            Pattern nr = Pattern.compile("(\\d+)|(\\d+.\\d+)"); 
            
            /// Punctuation has not been removed yet. If last character of input string is numeric, return whether the whole string matches 
            /// the pattern defined above:
            if (Character.isDigit(parts[parts.length - 1])){ 
                return nr.matcher(s).matches();
            } 
            
            /// If last character is neither numeric, nor alphabetic, it must be punctuation. 
            /// In that case omit the last character for checking whether string equals pattern above
            else if (parts.length > 1 && ! Character.isAlphabetic(parts[parts.length - 1])){ 
                return nr.matcher(s.substring(0, parts.length - 2)).matches();
            }
        } 
        /// If last character is alphabetic or string doesn't fit pattern, return null
        return false;
    }
    
    /// This method uses a pattern which checks whether the string passed to the method is a possibly valid e-mail-address, 
    /// following the structure of [someText.-+]@[provider].[ending with 2 or 3 letters, like ".de", ".nl" or ".com"]
    public boolean isEMailAddress(String str){
        String regex = "([a-zA-Z0-9._%+-]+@[a-zA-Z0-9._%+-]+.[a-zA-Z]{2,3})*";
        Pattern p = Pattern.compile(regex);
        return p.matcher(str).matches();
    }
    
    /// Main method to determine whether string is a "special case"
    private String getSpecialCases(String str){
        if (str.contains("www.") || str.contains("http")){ 
            return "internetaddress";
        }
        if (isEMailAddress(str)){
            return "emailaddress";
        }
        if (isNumeric(str)){
            return "number";
        }
        if (isDate(str)){
            return "concretedate";
        }
        
        return null;
    }
    
/// This method returnes cleaned text. Either a "Special Class"-value (e.g. if token is an e-mail-address or if it is a date) or a cleaned text alternatively    
    public String returnCleanText(String str){
        String ret = getSpecialCases(str);
        return (ret == null ? cleanText(str) : ret);
    }
}

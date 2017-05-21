package com.sizzler.common.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Hex;

import com.alibaba.fastjson.JSON;

public abstract class StringUtil {

  private static final String FOLDER_SEPARATOR = "/";

  private static final String WINDOWS_FOLDER_SEPARATOR = "\\";

  private static final String TOP_PATH = "..";

  private static final String CURRENT_PATH = ".";

  private static final char EXTENSION_SEPARATOR = '.';

  // ---------------------------------------------------------------------
  // General convenience methods for working with Strings
  // ---------------------------------------------------------------------

  /**
   * Check that the given CharSequence is neither <code>null</code> nor of
   * length 0. Note: Will return <code>true</code> for a CharSequence that
   * purely consists of whitespace.
   * <p>
   * 
   * <pre>
   * StringUtil.hasLength(null) = false
   * StringUtil.hasLength("") = false
   * StringUtil.hasLength(" ") = true
   * StringUtil.hasLength("Hello") = true
   * </pre>
   * 
   * @param str
   *          the CharSequence to check (may be <code>null</code>)
   * @return <code>true</code> if the CharSequence is not null and has length
   * @see #hasText(String)
   */
  public static boolean hasLength(CharSequence str) {
    return (str != null && str.length() > 0);
  }

  /**
   * Check that the given String is neither <code>null</code> nor of length 0.
   * Note: Will return <code>true</code> for a String that purely consists of
   * whitespace.
   * 
   * @param str
   *          the String to check (may be <code>null</code>)
   * @return <code>true</code> if the String is not null and has length
   * @see #hasLength(CharSequence)
   */
  public static boolean hasLength(String str) {
    return hasLength((CharSequence) str);
  }

  /**
   * Check whether the given CharSequence has actual text. More specifically,
   * returns <code>true</code> if the string not <code>null</code>, its length
   * is greater than 0, and it contains at least one non-whitespace character.
   * <p>
   * 
   * <pre>
   * StringUtil.hasText(null) = false
   * StringUtil.hasText("") = false
   * StringUtil.hasText(" ") = false
   * StringUtil.hasText("12345") = true
   * StringUtil.hasText(" 12345 ") = true
   * </pre>
   * 
   * @param str
   *          the CharSequence to check (may be <code>null</code>)
   * @return <code>true</code> if the CharSequence is not <code>null</code>, its
   *         length is greater than 0, and it does not contain whitespace only
   * @see Character#isWhitespace
   */
  public static boolean hasText(CharSequence str) {
    if (!hasLength(str)) {
      return false;
    }
    int strLen = str.length();
    for (int i = 0; i < strLen; i++) {
      if (!Character.isWhitespace(str.charAt(i))) {
        return true;
      }
    }
    return false;
  }

  /**
   * Check whether the given String has actual text. More specifically, returns
   * <code>true</code> if the string not <code>null</code>, its length is
   * greater than 0, and it contains at least one non-whitespace character.
   * 
   * @param str
   *          the String to check (may be <code>null</code>)
   * @return <code>true</code> if the String is not <code>null</code>, its
   *         length is greater than 0, and it does not contain whitespace only
   * @see #hasText(CharSequence)
   */
  public static boolean hasText(String str) {
    return hasText((CharSequence) str);
  }

  /**
   * Check whether the given CharSequence contains any whitespace characters.
   * 
   * @param str
   *          the CharSequence to check (may be <code>null</code>)
   * @return <code>true</code> if the CharSequence is not empty and contains at
   *         least 1 whitespace character
   * @see Character#isWhitespace
   */
  public static boolean containsWhitespace(CharSequence str) {
    if (!hasLength(str)) {
      return false;
    }
    int strLen = str.length();
    for (int i = 0; i < strLen; i++) {
      if (Character.isWhitespace(str.charAt(i))) {
        return true;
      }
    }
    return false;
  }

  /**
   * Check whether the given String contains any whitespace characters.
   * 
   * @param str
   *          the String to check (may be <code>null</code>)
   * @return <code>true</code> if the String is not empty and contains at least
   *         1 whitespace character
   * @see #containsWhitespace(CharSequence)
   */
  public static boolean containsWhitespace(String str) {
    return containsWhitespace((CharSequence) str);
  }

  /**
   * Trim leading and trailing whitespace from the given String.
   * 
   * @param str
   *          the String to check
   * @return the trimmed String
   * @see Character#isWhitespace
   */
  public static String trimWhitespace(String str) {
    if (!hasLength(str)) {
      return str;
    }
    StringBuilder sb = new StringBuilder(str);
    while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
      sb.deleteCharAt(0);
    }
    while (sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }

  /**
   * Trim <i>all</i> whitespace from the given String: leading, trailing, and
   * inbetween characters.
   * 
   * @param str
   *          the String to check
   * @return the trimmed String
   * @see Character#isWhitespace
   */
  public static String trimAllWhitespace(String str) {
    if (!hasLength(str)) {
      return str;
    }
    StringBuilder sb = new StringBuilder(str);
    int index = 0;
    while (sb.length() > index) {
      if (Character.isWhitespace(sb.charAt(index))) {
        sb.deleteCharAt(index);
      } else {
        index++;
      }
    }
    return sb.toString();
  }

  /**
   * Trim leading whitespace from the given String.
   * 
   * @param str
   *          the String to check
   * @return the trimmed String
   * @see Character#isWhitespace
   */
  public static String trimLeadingWhitespace(String str) {
    if (!hasLength(str)) {
      return str;
    }
    StringBuilder sb = new StringBuilder(str);
    while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
      sb.deleteCharAt(0);
    }
    return sb.toString();
  }

  /**
   * Trim trailing whitespace from the given String.
   * 
   * @param str
   *          the String to check
   * @return the trimmed String
   * @see Character#isWhitespace
   */
  public static String trimTrailingWhitespace(String str) {
    if (!hasLength(str)) {
      return str;
    }
    StringBuilder sb = new StringBuilder(str);
    while (sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }

  /**
   * Trim all occurences of the supplied leading character from the given
   * String.
   * 
   * @param str
   *          the String to check
   * @param leadingCharacter
   *          the leading character to be trimmed
   * @return the trimmed String
   */
  public static String trimLeadingCharacter(String str, char leadingCharacter) {
    if (!hasLength(str)) {
      return str;
    }
    StringBuilder sb = new StringBuilder(str);
    while (sb.length() > 0 && sb.charAt(0) == leadingCharacter) {
      sb.deleteCharAt(0);
    }
    return sb.toString();
  }

  /**
   * Trim all occurences of the supplied trailing character from the given
   * String.
   * 
   * @param str
   *          the String to check
   * @param trailingCharacter
   *          the trailing character to be trimmed
   * @return the trimmed String
   */
  public static String trimTrailingCharacter(String str, char trailingCharacter) {
    if (!hasLength(str)) {
      return str;
    }
    StringBuilder sb = new StringBuilder(str);
    while (sb.length() > 0 && sb.charAt(sb.length() - 1) == trailingCharacter) {
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }

  /**
   * Test if the given String starts with the specified prefix, ignoring
   * upper/lower case.
   * 
   * @param str
   *          the String to check
   * @param prefix
   *          the prefix to look for
   * @see String#startsWith
   */
  public static boolean startsWithIgnoreCase(String str, String prefix) {
    if (str == null || prefix == null) {
      return false;
    }
    if (str.startsWith(prefix)) {
      return true;
    }
    if (str.length() < prefix.length()) {
      return false;
    }
    String lcStr = str.substring(0, prefix.length()).toLowerCase();
    String lcPrefix = prefix.toLowerCase();
    return lcStr.equals(lcPrefix);
  }

  /**
   * Test if the given String ends with the specified suffix, ignoring
   * upper/lower case.
   * 
   * @param str
   *          the String to check
   * @param suffix
   *          the suffix to look for
   * @see String#endsWith
   */
  public static boolean endsWithIgnoreCase(String str, String suffix) {
    if (str == null || suffix == null) {
      return false;
    }
    if (str.endsWith(suffix)) {
      return true;
    }
    if (str.length() < suffix.length()) {
      return false;
    }

    String lcStr = str.substring(str.length() - suffix.length()).toLowerCase();
    String lcSuffix = suffix.toLowerCase();
    return lcStr.equals(lcSuffix);
  }

  /**
   * Test whether the given string matches the given substring at the given
   * index.
   * 
   * @param str
   *          the original string (or StringBuilder)
   * @param index
   *          the index in the original string to start matching against
   * @param substring
   *          the substring to match at the given index
   */
  public static boolean substringMatch(CharSequence str, int index, CharSequence substring) {
    for (int j = 0; j < substring.length(); j++) {
      int i = index + j;
      if (i >= str.length() || str.charAt(i) != substring.charAt(j)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Count the occurrences of the substring in string s.
   * 
   * @param str
   *          string to search in. Return 0 if this is null.
   * @param sub
   *          string to search for. Return 0 if this is null.
   */
  public static int countOccurrencesOf(String str, String sub) {
    if (str == null || sub == null || str.length() == 0 || sub.length() == 0) {
      return 0;
    }
    int count = 0;
    int pos = 0;
    int idx;
    while ((idx = str.indexOf(sub, pos)) != -1) {
      ++count;
      pos = idx + sub.length();
    }
    return count;
  }

  /**
   * Replace all occurences of a substring within a string with another string.
   * 
   * @param inString
   *          String to examine
   * @param oldPattern
   *          String to replace
   * @param newPattern
   *          String to insert
   * @return a String with the replacements
   */
  public static String replace(String inString, String oldPattern, String newPattern) {
    if (!hasLength(inString) || !hasLength(oldPattern) || newPattern == null) {
      return inString;
    }
    StringBuilder sb = new StringBuilder();
    int pos = 0; // our position in the old string
    int index = inString.indexOf(oldPattern);
    // the index of an occurrence we've found, or -1
    int patLen = oldPattern.length();
    while (index >= 0) {
      sb.append(inString.substring(pos, index));
      sb.append(newPattern);
      pos = index + patLen;
      index = inString.indexOf(oldPattern, pos);
    }
    sb.append(inString.substring(pos));
    // remember to append any characters to the right of a match
    return sb.toString();
  }

  /**
   * Delete all occurrences of the given substring.
   * 
   * @param inString
   *          the original String
   * @param pattern
   *          the pattern to delete all occurrences of
   * @return the resulting String
   */
  public static String delete(String inString, String pattern) {
    return replace(inString, pattern, "");
  }

  /**
   * Delete any character in a given String.
   * 
   * @param inString
   *          the original String
   * @param charsToDelete
   *          a set of characters to delete. E.g. "az\n" will delete 'a's, 'z's
   *          and new lines.
   * @return the resulting String
   */
  public static String deleteAny(String inString, String charsToDelete) {
    if (!hasLength(inString) || !hasLength(charsToDelete)) {
      return inString;
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < inString.length(); i++) {
      char c = inString.charAt(i);
      if (charsToDelete.indexOf(c) == -1) {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  // ---------------------------------------------------------------------
  // Convenience methods for working with formatted Strings
  // ---------------------------------------------------------------------

  /**
   * Quote the given String with single quotes.
   * 
   * @param str
   *          the input String (e.g. "myString")
   * @return the quoted String (e.g. "'myString'"), or
   *         <code>null<code> if the input was <code>null</code>
   */
  public static String quote(String str) {
    return (str != null ? "'" + str + "'" : null);
  }

  /**
   * Turn the given Object into a String with single quotes if it is a String;
   * keeping the Object as-is else.
   * 
   * @param obj
   *          the input Object (e.g. "myString")
   * @return the quoted String (e.g. "'myString'"), or the input object as-is if
   *         not a String
   */
  public static Object quoteIfString(Object obj) {
    return (obj instanceof String ? quote((String) obj) : obj);
  }

  /**
   * Unqualify a string qualified by a '.' dot character. For example,
   * "this.name.is.qualified", returns "qualified".
   * 
   * @param qualifiedName
   *          the qualified name
   */
  public static String unqualify(String qualifiedName) {
    return unqualify(qualifiedName, '.');
  }

  /**
   * Unqualify a string qualified by a separator character. For example,
   * "this:name:is:qualified" returns "qualified" if using a ':' separator.
   * 
   * @param qualifiedName
   *          the qualified name
   * @param separator
   *          the separator
   */
  public static String unqualify(String qualifiedName, char separator) {
    return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1);
  }

  /**
   * Capitalize a <code>String</code>, changing the first letter to upper case
   * as per {@link Character#toUpperCase(char)}. No other letters are changed.
   * 
   * @param str
   *          the String to capitalize, may be <code>null</code>
   * @return the capitalized String, <code>null</code> if null
   */
  public static String capitalize(String str) {
    return changeFirstCharacterCase(str, true);
  }

  /**
   * Uncapitalize a <code>String</code>, changing the first letter to lower case
   * as per {@link Character#toLowerCase(char)}. No other letters are changed.
   * 
   * @param str
   *          the String to uncapitalize, may be <code>null</code>
   * @return the uncapitalized String, <code>null</code> if null
   */
  public static String uncapitalize(String str) {
    return changeFirstCharacterCase(str, false);
  }

  private static String changeFirstCharacterCase(String str, boolean capitalize) {
    if (str == null || str.length() == 0) {
      return str;
    }
    StringBuilder sb = new StringBuilder(str.length());
    if (capitalize) {
      sb.append(Character.toUpperCase(str.charAt(0)));
    } else {
      sb.append(Character.toLowerCase(str.charAt(0)));
    }
    sb.append(str.substring(1));
    return sb.toString();
  }

  /**
   * Extract the filename from the given path, e.g. "mypath/myfile.txt" ->
   * "myfile.txt".
   * 
   * @param path
   *          the file path (may be <code>null</code>)
   * @return the extracted filename, or <code>null</code> if none
   */
  public static String getFilename(String path) {
    if (path == null) {
      return null;
    }
    int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
    return (separatorIndex != -1 ? path.substring(separatorIndex + 1) : path);
  }

  /**
   * Extract the filename extension from the given path, e.g.
   * "mypath/myfile.txt" -> "txt".
   * 
   * @param path
   *          the file path (may be <code>null</code>)
   * @return the extracted filename extension, or <code>null</code> if none
   */
  public static String getFilenameExtension(String path) {
    if (path == null) {
      return null;
    }
    int extIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
    if (extIndex == -1) {
      return null;
    }
    int folderIndex = path.lastIndexOf(FOLDER_SEPARATOR);
    if (folderIndex > extIndex) {
      return null;
    }
    return path.substring(extIndex + 1);
  }

  /**
   * Strip the filename extension from the given path, e.g. "mypath/myfile.txt"
   * -> "mypath/myfile".
   * 
   * @param path
   *          the file path (may be <code>null</code>)
   * @return the path with stripped filename extension, or <code>null</code> if
   *         none
   */
  public static String stripFilenameExtension(String path) {
    if (path == null) {
      return null;
    }
    int extIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
    if (extIndex == -1) {
      return path;
    }
    int folderIndex = path.lastIndexOf(FOLDER_SEPARATOR);
    if (folderIndex > extIndex) {
      return path;
    }
    return path.substring(0, extIndex);
  }

  /**
   * Apply the given relative path to the given path, assuming standard Java
   * folder separation (i.e. "/" separators).
   * 
   * @param path
   *          the path to start from (usually a full file path)
   * @param relativePath
   *          the relative path to apply (relative to the full file path above)
   * @return the full file path that results from applying the relative path
   */
  public static String applyRelativePath(String path, String relativePath) {
    int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
    if (separatorIndex != -1) {
      String newPath = path.substring(0, separatorIndex);
      if (!relativePath.startsWith(FOLDER_SEPARATOR)) {
        newPath += FOLDER_SEPARATOR;
      }
      return newPath + relativePath;
    } else {
      return relativePath;
    }
  }

  /**
   * Normalize the path by suppressing sequences like "path/.." and inner simple
   * dots.
   * <p>
   * The result is convenient for path comparison. For other uses, notice that
   * Windows separators ("\") are replaced by simple slashes.
   * 
   * @param path
   *          the original path
   * @return the normalized path
   */
  public static String cleanPath(String path) {
    if (path == null) {
      return null;
    }
    String pathToUse = replace(path, WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR);

    // Strip prefix from path to analyze, to not treat it as part of the
    // first path element. This is necessary to correctly parse paths like
    // "file:core/../core/io/Resource.class", where the ".." should just
    // strip the first "core" directory while keeping the "file:" prefix.
    int prefixIndex = pathToUse.indexOf(":");
    String prefix = "";
    if (prefixIndex != -1) {
      prefix = pathToUse.substring(0, prefixIndex + 1);
      pathToUse = pathToUse.substring(prefixIndex + 1);
    }
    if (pathToUse.startsWith(FOLDER_SEPARATOR)) {
      prefix = prefix + FOLDER_SEPARATOR;
      pathToUse = pathToUse.substring(1);
    }

    String[] pathArray = delimitedListToStringArray(pathToUse, FOLDER_SEPARATOR);
    List<String> pathElements = new LinkedList<String>();
    int tops = 0;

    for (int i = pathArray.length - 1; i >= 0; i--) {
      String element = pathArray[i];
      if (CURRENT_PATH.equals(element)) {
        // Points to current directory - drop it.
      } else if (TOP_PATH.equals(element)) {
        // Registering top path found.
        tops++;
      } else {
        if (tops > 0) {
          // Merging path element with element corresponding to top
          // path.
          tops--;
        } else {
          // Normal path element found.
          pathElements.add(0, element);
        }
      }
    }

    // Remaining top paths need to be retained.
    for (int i = 0; i < tops; i++) {
      pathElements.add(0, TOP_PATH);
    }

    return prefix + collectionToDelimitedString(pathElements, FOLDER_SEPARATOR);
  }

  /**
   * Compare two paths after normalization of them.
   * 
   * @param path1
   *          first path for comparison
   * @param path2
   *          second path for comparison
   * @return whether the two paths are equivalent after normalization
   */
  public static boolean pathEquals(String path1, String path2) {
    return cleanPath(path1).equals(cleanPath(path2));
  }

  /**
   * Parse the given <code>localeString</code> value into a
   * {@link java.util.Locale}.
   * <p>
   * This is the inverse operation of {@link java.util.Locale#toString Locale's
   * toString}.
   * 
   * @param localeString
   *          the locale string, following <code>Locale's</code>
   *          <code>toString()</code> format ("en", "en_UK", etc); also accepts
   *          spaces as separators, as an alternative to underscores
   * @return a corresponding <code>Locale</code> instance
   */
  public static Locale parseLocaleString(String localeString) {
    String[] parts = tokenizeToStringArray(localeString, "_ ", false, false);
    String language = (parts.length > 0 ? parts[0] : "");
    String country = (parts.length > 1 ? parts[1] : "");
    validateLocalePart(language);
    validateLocalePart(country);
    String variant = "";
    if (parts.length >= 2) {
      // There is definitely a variant, and it is everything after the
      // country
      // code sans the separator between the country code and the variant.
      int endIndexOfCountryCode = localeString.indexOf(country) + country.length();
      // Strip off any leading '_' and whitespace, what's left is the
      // variant.
      variant = trimLeadingWhitespace(localeString.substring(endIndexOfCountryCode));
      if (variant.startsWith("_")) {
        variant = trimLeadingCharacter(variant, '_');
      }
    }
    return (language.length() > 0 ? new Locale(language, country, variant) : null);
  }

  private static void validateLocalePart(String localePart) {
    for (int i = 0; i < localePart.length(); i++) {
      char ch = localePart.charAt(i);
      if (ch != '_' && ch != ' ' && !Character.isLetterOrDigit(ch)) {
        throw new IllegalArgumentException("Locale part \"" + localePart
            + "\" contains invalid characters");
      }
    }
  }

  /**
   * Determine the RFC 3066 compliant language tag, as used for the HTTP
   * "Accept-Language" header.
   * 
   * @param locale
   *          the Locale to transform to a language tag
   * @return the RFC 3066 compliant language tag as String
   */
  public static String toLanguageTag(Locale locale) {
    return locale.getLanguage() + (hasText(locale.getCountry()) ? "-" + locale.getCountry() : "");
  }

  // ---------------------------------------------------------------------
  // Convenience methods for working with String arrays
  // ---------------------------------------------------------------------

  /**
   * Append the given String to the given String array, returning a new array
   * consisting of the input array contents plus the given String.
   * 
   * @param array
   *          the array to append to (can be <code>null</code>)
   * @param str
   *          the String to append
   * @return the new array (never <code>null</code>)
   */
  public static String[] addStringToArray(String[] array, String str) {
    if (ObjectUtil.isEmpty(array)) {
      return new String[] { str };
    }
    String[] newArr = new String[array.length + 1];
    System.arraycopy(array, 0, newArr, 0, array.length);
    newArr[array.length] = str;
    return newArr;
  }

  /**
   * Concatenate the given String arrays into one, with overlapping array
   * elements included twice.
   * <p>
   * The order of elements in the original arrays is preserved.
   * 
   * @param array1
   *          the first array (can be <code>null</code>)
   * @param array2
   *          the second array (can be <code>null</code>)
   * @return the new array (<code>null</code> if both given arrays were
   *         <code>null</code>)
   */
  public static String[] concatenateStringArrays(String[] array1, String[] array2) {
    if (ObjectUtil.isEmpty(array1)) {
      return array2;
    }
    if (ObjectUtil.isEmpty(array2)) {
      return array1;
    }
    String[] newArr = new String[array1.length + array2.length];
    System.arraycopy(array1, 0, newArr, 0, array1.length);
    System.arraycopy(array2, 0, newArr, array1.length, array2.length);
    return newArr;
  }

  /**
   * Merge the given String arrays into one, with overlapping array elements
   * only included once.
   * <p>
   * The order of elements in the original arrays is preserved (with the
   * exception of overlapping elements, which are only included on their first
   * occurrence).
   * 
   * @param array1
   *          the first array (can be <code>null</code>)
   * @param array2
   *          the second array (can be <code>null</code>)
   * @return the new array (<code>null</code> if both given arrays were
   *         <code>null</code>)
   */
  public static String[] mergeStringArrays(String[] array1, String[] array2) {
    if (ObjectUtil.isEmpty(array1)) {
      return array2;
    }
    if (ObjectUtil.isEmpty(array2)) {
      return array1;
    }
    List<String> result = new ArrayList<String>();
    result.addAll(Arrays.asList(array1));
    for (String str : array2) {
      if (!result.contains(str)) {
        result.add(str);
      }
    }
    return toStringArray(result);
  }

  /**
   * Turn given source String array into sorted array.
   * 
   * @param array
   *          the source array
   * @return the sorted array (never <code>null</code>)
   */
  public static String[] sortStringArray(String[] array) {
    if (ObjectUtil.isEmpty(array)) {
      return new String[0];
    }
    Arrays.sort(array);
    return array;
  }

  /**
   * Copy the given Collection into a String array. The Collection must contain
   * String elements only.
   * 
   * @param collection
   *          the Collection to copy
   * @return the String array (<code>null</code> if the passed-in Collection was
   *         <code>null</code>)
   */
  public static String[] toStringArray(Collection<String> collection) {
    if (collection == null) {
      return null;
    }
    return collection.toArray(new String[collection.size()]);
  }

  /**
   * Copy the given Enumeration into a String array. The Enumeration must
   * contain String elements only.
   * 
   * @param enumeration
   *          the Enumeration to copy
   * @return the String array (<code>null</code> if the passed-in Enumeration
   *         was <code>null</code>)
   */
  public static String[] toStringArray(Enumeration<String> enumeration) {
    if (enumeration == null) {
      return null;
    }
    List<String> list = Collections.list(enumeration);
    return list.toArray(new String[list.size()]);
  }

  /**
   * Trim the elements of the given String array, calling
   * <code>String.trim()</code> on each of them.
   * 
   * @param array
   *          the original String array
   * @return the resulting array (of the same size) with trimmed elements
   */
  public static String[] trimArrayElements(String[] array) {
    if (ObjectUtil.isEmpty(array)) {
      return new String[0];
    }
    String[] result = new String[array.length];
    for (int i = 0; i < array.length; i++) {
      String element = array[i];
      result[i] = (element != null ? element.trim() : null);
    }
    return result;
  }

  /**
   * Remove duplicate Strings from the given array. Also sorts the array, as it
   * uses a TreeSet.
   * 
   * @param array
   *          the String array
   * @return an array without duplicates, in natural sort order
   */
  public static String[] removeDuplicateStrings(String[] array) {
    if (ObjectUtil.isEmpty(array)) {
      return array;
    }
    Set<String> set = new TreeSet<String>();
    for (String element : array) {
      set.add(element);
    }
    return toStringArray(set);
  }

  /**
   * Split a String at the first occurrence of the delimiter. Does not include
   * the delimiter in the result.
   * 
   * @param toSplit
   *          the string to split
   * @param delimiter
   *          to split the string up with
   * @return a two element array with index 0 being before the delimiter, and
   *         index 1 being after the delimiter (neither element includes the
   *         delimiter); or <code>null</code> if the delimiter wasn't found in
   *         the given input String
   */
  public static String[] split(String toSplit, String delimiter) {
    if (!hasLength(toSplit) || !hasLength(delimiter)) {
      return null;
    }
    int offset = toSplit.indexOf(delimiter);
    if (offset < 0) {
      return null;
    }
    String beforeDelimiter = toSplit.substring(0, offset);
    String afterDelimiter = toSplit.substring(offset + delimiter.length());
    return new String[] { beforeDelimiter, afterDelimiter };
  }

  public static List<String> splitToList(String toSplit, String delimiterRegex) {
    List<String> strList = null;
    if (!hasLength(toSplit) || !hasLength(delimiterRegex)) {
      return strList;
    }
    String[] strs = toSplit.split(delimiterRegex);
    strList = new ArrayList<String>();
    for (String str : strs) {
      strList.add(str);
    }
    return strList;
  }

  /**
   * Take an array Strings and split each element based on the given delimiter.
   * A <code>Properties</code> instance is then generated, with the left of the
   * delimiter providing the key, and the right of the delimiter providing the
   * value.
   * <p>
   * Will trim both the key and value before adding them to the
   * <code>Properties</code> instance.
   * 
   * @param array
   *          the array to process
   * @param delimiter
   *          to split each element using (typically the equals symbol)
   * @return a <code>Properties</code> instance representing the array contents,
   *         or <code>null</code> if the array to process was null or empty
   */
  public static Properties splitArrayElementsIntoProperties(String[] array, String delimiter) {
    return splitArrayElementsIntoProperties(array, delimiter, null);
  }

  /**
   * Take an array Strings and split each element based on the given delimiter.
   * A <code>Properties</code> instance is then generated, with the left of the
   * delimiter providing the key, and the right of the delimiter providing the
   * value.
   * <p>
   * Will trim both the key and value before adding them to the
   * <code>Properties</code> instance.
   * 
   * @param array
   *          the array to process
   * @param delimiter
   *          to split each element using (typically the equals symbol)
   * @param charsToDelete
   *          one or more characters to remove from each element prior to
   *          attempting the split operation (typically the quotation mark
   *          symbol), or <code>null</code> if no removal should occur
   * @return a <code>Properties</code> instance representing the array contents,
   *         or <code>null</code> if the array to process was <code>null</code>
   *         or empty
   */
  public static Properties splitArrayElementsIntoProperties(String[] array, String delimiter,
      String charsToDelete) {

    if (ObjectUtil.isEmpty(array)) {
      return null;
    }
    Properties result = new Properties();
    for (String element : array) {
      if (charsToDelete != null) {
        element = deleteAny(element, charsToDelete);
      }
      String[] splittedElement = split(element, delimiter);
      if (splittedElement == null) {
        continue;
      }
      result.setProperty(splittedElement[0].trim(), splittedElement[1].trim());
    }
    return result;
  }

  /**
   * Tokenize the given String into a String array via a StringTokenizer. Trims
   * tokens and omits empty tokens.
   * <p>
   * The given delimiters string is supposed to consist of any number of
   * delimiter characters. Each of those characters can be used to separate
   * tokens. A delimiter is always a single character; for multi-character
   * delimiters, consider using <code>delimitedListToStringArray</code>
   * 
   * @param str
   *          the String to tokenize
   * @param delimiters
   *          the delimiter characters, assembled as String (each of those
   *          characters is individually considered as delimiter).
   * @return an array of the tokens
   * @see java.util.StringTokenizer
   * @see String#trim()
   * @see #delimitedListToStringArray
   */
  public static String[] tokenizeToStringArray(String str, String delimiters) {
    return tokenizeToStringArray(str, delimiters, true, true);
  }

  /**
   * Tokenize the given String into a String array via a StringTokenizer.
   * <p>
   * The given delimiters string is supposed to consist of any number of
   * delimiter characters. Each of those characters can be used to separate
   * tokens. A delimiter is always a single character; for multi-character
   * delimiters, consider using <code>delimitedListToStringArray</code>
   * 
   * @param str
   *          the String to tokenize
   * @param delimiters
   *          the delimiter characters, assembled as String (each of those
   *          characters is individually considered as delimiter)
   * @param trimTokens
   *          trim the tokens via String's <code>trim</code>
   * @param ignoreEmptyTokens
   *          omit empty tokens from the result array (only applies to tokens
   *          that are empty after trimming; StringTokenizer will not consider
   *          subsequent delimiters as token in the first place).
   * @return an array of the tokens (<code>null</code> if the input String was
   *         <code>null</code>)
   * @see java.util.StringTokenizer
   * @see String#trim()
   * @see #delimitedListToStringArray
   */
  public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens,
      boolean ignoreEmptyTokens) {

    if (str == null) {
      return null;
    }
    StringTokenizer st = new StringTokenizer(str, delimiters);
    List<String> tokens = new ArrayList<String>();
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      if (trimTokens) {
        token = token.trim();
      }
      if (!ignoreEmptyTokens || token.length() > 0) {
        tokens.add(token);
      }
    }
    return toStringArray(tokens);
  }

  /**
   * Take a String which is a delimited list and convert it to a String array.
   * <p>
   * A single delimiter can consists of more than one character: It will still
   * be considered as single delimiter string, rather than as bunch of potential
   * delimiter characters - in contrast to <code>tokenizeToStringArray</code>.
   * 
   * @param str
   *          the input String
   * @param delimiter
   *          the delimiter between elements (this is a single delimiter, rather
   *          than a bunch individual delimiter characters)
   * @return an array of the tokens in the list
   * @see #tokenizeToStringArray
   */
  public static String[] delimitedListToStringArray(String str, String delimiter) {
    return delimitedListToStringArray(str, delimiter, null);
  }

  /**
   * Take a String which is a delimited list and convert it to a String array.
   * <p>
   * A single delimiter can consists of more than one character: It will still
   * be considered as single delimiter string, rather than as bunch of potential
   * delimiter characters - in contrast to <code>tokenizeToStringArray</code>.
   * 
   * @param str
   *          the input String
   * @param delimiter
   *          the delimiter between elements (this is a single delimiter, rather
   *          than a bunch individual delimiter characters)
   * @param charsToDelete
   *          a set of characters to delete. Useful for deleting unwanted line
   *          breaks: e.g. "\r\n\f" will delete all new lines and line feeds in
   *          a String.
   * @return an array of the tokens in the list
   * @see #tokenizeToStringArray
   */
  public static String[] delimitedListToStringArray(String str, String delimiter,
      String charsToDelete) {
    if (str == null) {
      return new String[0];
    }
    if (delimiter == null) {
      return new String[] { str };
    }
    List<String> result = new ArrayList<String>();
    if ("".equals(delimiter)) {
      for (int i = 0; i < str.length(); i++) {
        result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
      }
    } else {
      int pos = 0;
      int delPos;
      while ((delPos = str.indexOf(delimiter, pos)) != -1) {
        result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
        pos = delPos + delimiter.length();
      }
      if (str.length() > 0 && pos <= str.length()) {
        // Add rest of String, but not in case of empty input.
        result.add(deleteAny(str.substring(pos), charsToDelete));
      }
    }
    return toStringArray(result);
  }

  /**
   * Convert a CSV list into an array of Strings.
   * 
   * @param str
   *          the input String
   * @return an array of Strings, or the empty array in case of empty input
   */
  public static String[] commaDelimitedListToStringArray(String str) {
    return delimitedListToStringArray(str, ",");
  }

  /**
   * Convenience method to convert a CSV string list to a set. Note that this
   * will suppress duplicates.
   * 
   * @param str
   *          the input String
   * @return a Set of String entries in the list
   */
  public static Set<String> commaDelimitedListToSet(String str) {
    Set<String> set = new TreeSet<String>();
    String[] tokens = commaDelimitedListToStringArray(str);
    for (String token : tokens) {
      set.add(token);
    }
    return set;
  }

  /**
   * Convenience method to return a Collection as a delimited (e.g. CSV) String.
   * E.g. useful for <code>toString()</code> implementations.
   * 
   * @param coll
   *          the Collection to display
   * @param delim
   *          the delimiter to use (probably a ",")
   * @param prefix
   *          the String to start each element with
   * @param suffix
   *          the String to end each element with
   * @return the delimited String
   */
  public static String collectionToDelimitedString(Collection<?> coll, String delim, String prefix,
      String suffix) {
    if (CollectionUtil.isEmpty(coll)) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    Iterator<?> it = coll.iterator();
    while (it.hasNext()) {
      sb.append(prefix).append(it.next()).append(suffix);
      if (it.hasNext()) {
        sb.append(delim);
      }
    }
    return sb.toString();
  }

  /**
   * Convenience method to return a Collection as a delimited (e.g. CSV) String.
   * E.g. useful for <code>toString()</code> implementations.
   * 
   * @param coll
   *          the Collection to display
   * @param delim
   *          the delimiter to use (probably a ",")
   * @return the delimited String
   */
  public static String collectionToDelimitedString(Collection<?> coll, String delim) {
    return collectionToDelimitedString(coll, delim, "", "");
  }

  /**
   * Convenience method to return a Collection as a CSV String. E.g. useful for
   * <code>toString()</code> implementations.
   * 
   * @param coll
   *          the Collection to display
   * @return the delimited String
   */
  public static String collectionToCommaDelimitedString(Collection<?> coll) {
    return collectionToDelimitedString(coll, ",");
  }

  /**
   * Convenience method to return a String array as a delimited (e.g. CSV)
   * String. E.g. useful for <code>toString()</code> implementations.
   * 
   * @param arr
   *          the array to display
   * @param delim
   *          the delimiter to use (probably a ",")
   * @return the delimited String
   */
  public static String arrayToDelimitedString(Object[] arr, String delim) {
    if (ObjectUtil.isEmpty(arr)) {
      return "";
    }
    if (arr.length == 1) {
      return ObjectUtil.nullSafeToString(arr[0]);
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < arr.length; i++) {
      if (i > 0) {
        sb.append(delim);
      }
      sb.append(arr[i]);
    }
    return sb.toString();
  }

  /**
   * Convenience method to return a String array as a CSV String. E.g. useful
   * for <code>toString()</code> implementations.
   * 
   * @param arr
   *          the array to display
   * @return the delimited String
   */
  public static String arrayToCommaDelimitedString(Object[] arr) {
    return arrayToDelimitedString(arr, ",");
  }

  public static String[] getMiddle(String uri) {
    String[] temp = uri.split(FOLDER_SEPARATOR);
    return temp;
  }

  /**
   * @param s
   * @return 如果<tt>s</tt>为<tt>null</tt>或空白字符串返回<tt>true</tt>
   */
  public static boolean isBlank(String s) {
    return s == null ? true : s.trim().length() == 0;
  }

  public static boolean isNotBlank(String... strs) {
    boolean isNotNull = true;
    if (strs.length == 0) {
      return false;
    }

    for (String str : strs) {
      if (StringUtil.isBlank(str)) {
        isNotNull = false;
        break;
      }
    }
    return isNotNull;
  }

  /**
   * 截取字符串，按照系统默认的字符集，截取后的后缀为“...”
   * 
   * @param target
   *          被截取的原字符串，此方法执行前会先<tt>trim</tt>
   * @param maxBytes
   *          截取后字符串的最大<tt>byte</tt>数，包括截取后的字符串的后缀
   * @see #substring(String, String, int, String)
   * @return
   */
  public static String substring(String target, int maxBytes) {
    return substring(target.trim(), Charset.defaultCharset().name(), maxBytes, "...");
  }

  /**
   * 截取字符串
   * 
   * @param target
   *          被截取的原字符串
   * @param charset
   *          字符串的字符集
   * @param maxBytes
   *          截取后字符串的最大<tt>byte</tt>数，包括截取后的字符串的后缀
   * @param append
   *          字符串被截去后的后缀
   * @return
   */
  public static String substring(String target, String charset, int maxBytes, String append) {
    try {
      int count = getBytes(target, charset).length;
      if (count <= maxBytes) {
        return target;
      } else {
        int bytesCount = 0;
        char[] replace = new char[getBytes(append, charset).length];
        int j = 0;
        int bound = maxBytes - getBytes(append, charset).length;
        for (int i = 0; i < target.length(); i++) {
          char c = target.charAt(i);
          bytesCount = c > 255 ? bytesCount + 2 : bytesCount + 1;
          if (bytesCount > maxBytes) {
            return target.substring(0, i - j).concat(append);
          }
          if (bytesCount > bound) {
            replace[j++] = c;
          }
        }
      }
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    throw new RuntimeException("Unreachable!");
  }

  public static String substring(String src, int start_idx, int end_idx) {
    byte[] b = src.getBytes();
    String tgt = "";
    for (int i = start_idx; i <= end_idx; i++) {
      tgt += (char) b[i];
    }
    return tgt;
  }

  private static byte[] getBytes(String s, String charset) throws UnsupportedEncodingException {
    return s.getBytes(charset);
  }

  public static String GBKToUTF(String str) {
    String utfStr = null;
    try {
      utfStr = new String(str.getBytes("GBK"), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return utfStr;
  }

  public static String UTFToGBK(String str) {
    String utfStr = null;
    try {
      utfStr = new String(str.getBytes("UTF-8"), "GBK");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return utfStr;
  }

  public static String ISOToGBK(String str) {
    String utfStr = null;
    try {
      utfStr = new String(str.getBytes("ISO8859_1"), "GBK");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return utfStr;
  }

  public static String getPasWordStr(String password) {
    String passStr = "";
    MessageDigest digester = null;
    try {
      digester = MessageDigest.getInstance("SHA");
      digester.update(password.getBytes("utf-8"));
      byte[] bytes = digester.digest();
      passStr = new String(Hex.encodeHex(bytes));
    } catch (Exception e) {
      e.printStackTrace();
    }

    return passStr;
  }

  /**
   * 将字符转换成map {row:20000,pageno:1,startdate:2008-9-10,enddate:2008-10-1}
   * 
   * @author hudaowan
   * @date Nov 3, 2008 10:03:26 AM
   * @return
   */
  public static Map<String, String> strToMap(String str) {
    Map<String, String> map = new HashMap<String, String>();
    if (!isBlank(str)) {
      String strObj = str.substring(1, str.length() - 1);
      String[] obj = strObj.split(",");
      for (String value : obj) {
        String[] key = value.split(":");
        map.put(key[0], key[1]);
      }
    }
    return map;
  }

  /**
   * 将map转换成字符
   * 
   * @author hudaowan
   * @date Nov 3, 2008 10:21:31 AM
   * @param map
   * @return
   */
  public static String mapToStr(Map<String, String> map) {
    String str = "";
    if (map != null) {
      for (Map.Entry<String, String> obj : map.entrySet()) {
        String key = obj.getKey();
        String value = obj.getValue();
        str += key + ":" + value + ",";
      }
    }
    if (str.length() > 0) {
      str = "{" + str.substring(0, str.length() - 1) + "}";
    }
    return str;
  }

  /**
   * 用<tt>seperator</tt>连接字符串,例如将数组{"a","b","c"}使用';'连接,得到"a;b;c",忽略
   * <tt>null<tt>和空白字符串
   * 
   * @see #join(String[], String, boolean, boolean)
   * @param s
   *          需要连接的字符串数组
   * @param seperator
   *          分隔符
   * @return 连接好的字符串或""
   * @throws NullPointerException
   *           如果<tt>s</tt>或<tt>seperator</tt>为<tt>null</tt>
   */
  public static String join(String[] s, String seperator) {
    return join(s, seperator, true, true);
  }

  /**
   * 用<tt>seperator</tt>连接字符串,例如将字符串列表使用'，'连接,得到"a,b,c",忽略 <tt>null<tt>和空白字符串
   * 
   * @see #join(List<String>, String, boolean, boolean)
   * @param strList
   *          需要连接的字符串列表
   * @param seperator
   *          分隔符
   * @return 连接好的字符串或""
   * @throws NullPointerException
   *           如果<tt>strList</tt>或<tt>seperator</tt>为<tt>null</tt>
   */
  public static String join(List<String> strList, String seperator) {
    return join(strList, seperator, true, true);
  }

  /**
   * 用<tt>seperator</tt>连接字符串,例如将数组{"a","b","c"}使用';'连接，得到"a;b;c"
   * 
   * @param s
   *          需要连接的字符串数组
   * @param seperator
   *          分隔符
   * @param ignoreBlank
   *          是否忽略空字符串,通过<tt>String.trim().length() == 0</tt>判断空字符串
   * @param ignoreNull
   *          是否忽略<tt>null</tt>
   * @return 连接好的字符串或""
   * @throws NullPointerException
   *           如果<tt>s</tt>或<tt>seperator</tt>为<tt>null</tt>
   */
  public static String join(String[] s, String seperator, boolean ignoreBlank, boolean ignoreNull) {
    if (s == null || seperator == null)
      throw new NullPointerException();
    StringBuilder result = new StringBuilder(256);
    for (String s_ : s) {
      if (ignoreNull && s_ == null)
        continue;
      else if (ignoreBlank && s_.trim().length() == 0)
        continue;
      result.append(s_);
      result.append(seperator);
    }
    int i = result.length();
    if (i > 0)
      return result.substring(0, i - seperator.length());
    else
      return "";
  }

  /**
   * 用<tt>seperator</tt>连接字符串,例如将字符串列表使用'，'连接,得到"a,b,c",忽略
   * 
   * @param strList
   *          需要连接的字符串数组
   * @param seperator
   *          分隔符
   * @param ignoreBlank
   *          是否忽略空字符串,通过<tt>String.trim().length() == 0</tt>判断空字符串
   * @param ignoreNull
   *          是否忽略<tt>null</tt>
   * @return 连接好的字符串或""
   * @throws NullPointerException
   *           如果<tt>s</tt>或<tt>seperator</tt>为<tt>null</tt>
   */
  public static String join(List<String> strList, String seperator, boolean ignoreBlank,
      boolean ignoreNull) {
    if (strList == null || seperator == null)
      throw new NullPointerException();
    StringBuilder result = new StringBuilder(256);
    for (String s_ : strList) {
      if (ignoreNull && s_ == null)
        continue;
      else if (ignoreBlank && s_.trim().length() == 0)
        continue;
      result.append(s_);
      result.append(seperator);
    }
    int i = result.length();
    if (i > 0)
      return result.substring(0, i - seperator.length());
    else
      return "";
  }

  /**
   * 将CamelCase转换成大写字母，以“_”为间隔，例如abcFoo转换成ABC_FOO
   * 
   * @param s
   * @return
   */
  public static String camelToCapital(String s) {
    final String pattern = "[A-Z]*[a-z0-9]+|[A-Z0-9]+";
    Pattern p = Pattern.compile(pattern); // the expression
    Matcher m = p.matcher(s); // the source
    String r = null;
    while (m.find()) {
      if (r != null) {
        r = r + "_" + m.group().toUpperCase();
      } else
        r = m.group().toUpperCase();
    }
    return r;
  }

  /**
   * 将大写字母转换成CamelCase，以“_”为间隔，例如ABC_FOO转换成abcFoo
   * 
   * @param s
   * @return
   */
  public static String capitalToCamel(String s) {
    String[] tokens = s.split("_");
    String r = tokens[0].toLowerCase();
    for (int i = 1; i < tokens.length; i++) {
      r += (tokens[i].substring(0, 1) + tokens[i].substring(1).toLowerCase());
    }
    return r;
  }

  public static String trim(String str) {
    str = str.replace('　', ' ');
    return str.trim();
  }

  public static Object convertStringToDataType(String value, String dataType) {
    // 如果结果返回null，向上也返回null，不要返回0，因为0也是一个值不同于null
    if ("INTEGER".equals(dataType)) {
      return Integer.valueOf(value);
    } else if ("LONG".equals(dataType)) {
      return Long.valueOf(value);
    } else if ("DOUBLE".equals(dataType) || "FLOAT".equals(dataType) || "PERCENT".equals(dataType)
        || "CURRENCY".equals(dataType) || "TIME".equals(dataType)) {
      BigDecimal fixValue = new BigDecimal(value);
      return fixValue.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(); // 四舍五入，保留两位小数
    } else {
      return value;
    }
  }

  /**
   * 根据数字生成excel列表头，最大到zz
   * 
   * @param i
   * @return
   */
  public static String i2s(int i) {
    String s = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z";
    String sArray[] = s.split(" ");
    if (i < 1)
      return "";
    if ((i / 26) == 0)
      return sArray[i % 26 - 1];
    else {
      if (i % 26 == 0)
        return (i2s((i / 26) - 1)) + sArray[26 - 1];
      else
        return sArray[(i / 26) - 1] + sArray[i % 26 - 1];
    }
  }

  /**
   * 根据数字生成excel列表头，最大受int最大值限制
   * 
   * @param num
   * @return
   */
  public static String getExcelColumnLabel(int num) {
    String temp = "";
    double i = Math.floor(Math.log(25.0 * (num) / 26.0 + 1) / Math.log(26)) + 1;
    if (i > 1) {
      double sub = num - 26 * (Math.pow(26, i - 1) - 1) / 25;
      for (double j = i; j > 0; j--) {
        temp = temp + (char) (sub / Math.pow(26, j - 1) + 65);
        sub = sub % Math.pow(26, j - 1);
      }
    } else {
      temp = temp + (char) (num + 65);
    }
    return temp;
  }

  public static String firstChartoUpperCase(String str) {
    return str.replaceFirst(str.substring(0, 1), str.substring(0, 1).toUpperCase());
  }

  /**
   * 科学技术法转为正常数字，例如：1.24E7转为12400000
   * 
   * @param str
   * @return
   */
  public static String scientificNotationToString(String str) {
    if (RegexUtils.isScientificNotation(str)) {
      BigDecimal db = new BigDecimal(str);
      return db.toPlainString();
    } else {
      return str;
    }
  }

  /**
   * 科学技术法转为正常数字，例如：1.24E7转为12400000
   * 
   * @param str
   * @return
   */
  public static String kxjsConvert(String str) {
    if (isKxjs(str)) {
      BigDecimal db = new BigDecimal(str);
      return db.toPlainString();
    } else {
      return str;
    }
  }

  /**
   * 验证当前字符串是否是科学计数法
   */
  public static boolean isKxjs(String str) {
    return str != null && str.matches("^((-?\\d+\\.?\\d+)[Ee]{1}([-+]?\\d+))$");
  }

  public static boolean isNumber(String str) {
    return StringUtil.isNotBlank(str) && str.matches("^(\\-|\\+)?\\d+(\\.\\d+)?$");
  }

  public static boolean isBigDecimal(Object value) {
    try {
      if (value != null) {
        new BigDecimal(String.valueOf(value));
        return true;
      }
    } catch (Exception e) {
    }
    return false;
  }

  public static boolean isDateStr(String str, String dateFormat) {
    boolean convertSuccess = true;
    SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.US);
    try {
      format.setLenient(false);// 设置lenient为false.
                               // 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
      format.parse(str);
    } catch (Exception e) {
      convertSuccess = false;
    }
    return convertSuccess;
  }

  /**
   * 转义字符串中的正则表达式使用的特殊字符
   */
  public static String escapeRegexp(String str) {
    if (StringUtil.isNotBlank(str)) {
      String[] charArray = new String[] { "*", ".", "?", "+", "$", "^", "[", "]", "(", ")", "{",
          "}", "|", "\\", "!" };
      for (String s : charArray) {
        str = str.replace(s, "\\" + s);
      }
    }
    return str;
  }

  /**
   * 获取json字符串中对应属性的值
   * 
   * @param jsonStr
   * @param key
   * @param jsonRegex
   *          json对应的正则表达式规则， 如果不为空则需要校验正则匹配
   */
  public static String getValueOfJson(String jsonStr, String key, String jsonRegex) {
    String value = null;
    if (StringUtil.isNotBlank(jsonStr) && StringUtil.isNotBlank(key)) {
      if (StringUtil.isBlank(jsonRegex)
          || (StringUtil.isNotBlank(jsonRegex) && jsonStr.matches(jsonRegex))) {
        try {
          Object valueObj = JSON.parseObject(jsonStr).get(key);
          if (valueObj != null) {
            value = valueObj.toString();
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    return value;
  }

}

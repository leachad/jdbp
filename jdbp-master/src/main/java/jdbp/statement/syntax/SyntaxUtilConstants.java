package jdbp.statement.syntax;

public class SyntaxUtilConstants {
	public static final char UNDERSCORE = '_';
	public static final String DOUBLE_QUOTED_SINGLE_QUOTE = "'";
	public static final char COMMA = ',';
	public static final String NO_SPACE = "";

	public static final class RegexConstants {
		public static final String OPERATION_PATTERN = "operation#[A-Za-z]+";
		public static final String KEYWORD_PATTERN = "keyword#[A-Za-z]+";
		public static final String DELIMITER_PATTERN = "delimiter#[/[/(,.;)/]/=]+";
		public static final String DYNAMIC_VALUE_PATTERN = "<[A-Za-z._]+>+";
		public static final String PLAIN_TEXT_PATTERN = "[A-Za-z]+";
		public static final String CLAUSE_PATTERN = "clause#[A-Za-z_]+";
	}

}

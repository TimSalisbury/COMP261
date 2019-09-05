import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A new instance of LempelZiv is created for every run.
 */
public class LempelZiv {

	private static final int WINDOW_SIZE = 10240;

	/**
	 * Take uncompressed input as a text string, compress it, and return it as a
	 * text string.
	 */
	public String compress(String input) {
		List<Tuple> encodedText = new ArrayList<>();
		int cursor = 0;
		int lookAhead = 0;
		int tLength = input.length();
		while(cursor < tLength){
			int location;
			int length = 0;
			lookAhead = (cursor + lookAhead >= tLength ? tLength : cursor + lookAhead);
			String searchWindow = input.substring(constrain(tLength, 0, cursor - WINDOW_SIZE), cursor);
			String match = input.substring(cursor, constrain(tLength, 0, cursor + length));


			if(searchWindow.contains(match)){
				length++;
				while(length <= lookAhead){
					if(cursor + length >= tLength){
						length = 1;
						break;
					}
					match = input.substring(cursor, constrain(tLength, 0, cursor + length));
					location = searchWindow.indexOf(match);
					if(length + cursor < tLength && location > -1){
						length++;
					}else{
						break;
					}
				}

				length--;
				match = input.substring(cursor, constrain(tLength, 0, cursor + length));
				location = searchWindow.indexOf(match);


				cursor += length;

				char character = input.charAt(cursor);
				int offset = WINDOW_SIZE - location;

				if(length + WINDOW_SIZE >= cursor) offset = cursor - location - length;

				encodedText.add(new Tuple(character, offset, length));
			}else{
				char character = input.charAt(cursor);

				encodedText.add(new Tuple(character, 0, 0));
			}
			cursor++;
		}


		StringBuilder builder = new StringBuilder();

		for(Tuple tuple : encodedText){
			builder.append(tuple);
		}

		return builder.toString();
	}

	private static int constrain(int max, int min, int value){
		return (value > max) ? max : ((value < min) ? min : value);
	}

	/**
	 * Take compressed input as a text string, decompress it, and return it as a
	 * text string.
	 */
	public String decompress(String compressed) {
		StringBuilder builder = new StringBuilder();

		List<Tuple> tuples = new ArrayList<>();
		String[] split = compressed.split("]\\[|\\[");

		for(String string : split){
			if(string.length() == 0) continue;

			String[] ssplit = string.split("\\|");

			tuples.add(new Tuple(ssplit[2].charAt(0), Integer.valueOf(ssplit[0]), Integer.valueOf(ssplit[1])));
		}

		for(Tuple tuple : tuples){

			if(tuple.getLength() == 0){
				builder.append(tuple.getCharacter());
			}else{
				for(int j = 0; j < tuple.getLength(); j++){
					builder.append(builder.charAt(builder.length() - tuple.getOffset()));
				}
				builder.append(tuple.getCharacter());
			}
		}


		return builder.toString();
	}

	/**
	 * The getInformation method is here for your convenience, you don't need to
	 * fill it in if you don't want to. It is called on every run and its return
	 * value is displayed on-screen. You can use this to print out any relevant
	 * information from your compression.
	 */
	public String getInformation() {
		return "";
	}

	private static class Tuple{

		private char character;
		private int offset;
		private int length;

		public Tuple(char character, int offset, int length) {
			this.character = character;
			this.offset = offset;
			this.length = length;
		}

		public char getCharacter() {
			return character;
		}

		public int getOffset() {
			return offset;
		}

		public int getLength() {
			return length;
		}

		@Override
		public String toString() {
			return "[" + offset + "|" + length + "|" + character + "]";
		}
	}
}

package jdbp.model;

import java.util.function.Function;

/**
 * @author andrew.leach
 * @param <R>
 * @param <T>
 */
public abstract class DBInfo<T, R> {

	private Function<T, R> toStringMethod;

	public DBInfo(Function<T, R> toStringMethod) {
		this.toStringMethod = toStringMethod;
	}

	@Override
	public String toString() {
		return null;
	}

}

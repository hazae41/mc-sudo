package hazae41.sudo;

import java.util.List;
import java.util.Optional;

public class Utils {
  static <T> Optional<T> get(T[] array, int index) {
    try {
      return Optional.of(array[index]);
    } catch (IndexOutOfBoundsException ex) {
      return Optional.empty();
    }
  }

  static <T> Optional<T> get(List<T> list, int index) {
    try {
      return Optional.of(list.get(index));
    } catch (IndexOutOfBoundsException ex) {
      return Optional.empty();
    }
  }

}

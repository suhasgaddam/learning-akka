case class Author(name: String)

case class Hashtag(name: String) {
  require(name.startsWith("#"), "Hash tag must starts with #.")
}

case class Tweet(author: Author, body: String) {
  def hashtags: Set[Hashtag] = {
    body.split(" ").collect {
      case word if word.startsWith("#") => Hashtag(word)
    }
  }.toSet
}

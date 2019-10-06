# Wicket LivePage

Wicket LivePage enables rich, real-time user experiences with server
rendered HTML powered by [Apache Wicket][wicket] and
[Kotlin][kotlin]. It was inspired by [Phoenix LiveView][phoenix] and
[Elm][elm].


## Motivation

While implementing user interfaces with Wicket, you will find yourself
in need of re-rendering only parts of a page. At this point, you will
drop in to AJAX-land and need to store references to components in
your page to selectively re-render them.

This is very efficient, but also very brittle and becomes complex
fast.  Instead the idea of [React][react], to always re-render
everything and let the program figure out what to actually do, is a
far superior programming model. 

This library enables this style for Wicket via Websockets.

So here is a simplified example for LiveView.


```kotlin
class MyLivePage: LivePage<Model, Msg>() {

  data class Model(
    val value: Int
  ): Serializable

  override fun init(): Int = 0
  
  override fun view(model: Model): Html<Msg> =
    div(
      text(model.value.toString()),
      button(
        listOf(onClick(Msg.Inc)),
        text("Increment")
      ),
      button(
        listOf(onClick(Msg.Dec)),
        text("Decrement")
      )
    )
  
  sealed class Msg: Serializable {
    object Inc: Msg()
    object Dec: Msg()
  }
  
  override fun update(msg: Msg, model: Int): Int = when (msg) {
    is Msg.Inc -> model.copy(value = model.value + 1)
    is Msg.Dec -> model.copy(value = model.value - 1)
  }
  
}
```


[wicket]: https://wicket.apache.org/
[phoenix]: https://github.com/phoenixframework/phoenix_live_view
[elm]: https://elm-lang.org/
[kotlin]: https://kotlinlang.org/
[react]: https://reactjs.org

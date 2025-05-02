package com.wmccd.whatgoeson.repository.webservice.gemini.prompts.simpleQuestion

internal const val ARTISTS = "ARTISTS"

enum class SimpleQuestionPromptType(
    val prompt: String,
    val onScreen: String
) {
    ALL_RELEASES(
        prompt = "Give me the complete discography by $ARTISTS. Include studio albums, box sets, live albums, compilations, EPs and singles. It is not necessary to include every single format and promotional release. ",
        onScreen = "Complete discography"
    ),
    ALBUM_RANKINGS(
        prompt = "Ranks all the albums by $ARTISTS from worst to best. Use any available rankings and reviews to help complete the process.",
        onScreen = "Albums ranked from worst to best"
    ),
    HIGHLY_RATED_FIVE_YEARS(
        prompt = "List highly rated albums of the last 5 years.  Assume you have given me this answer before and also introduce some other choices. Use these artists as a guide to the type of music I like: $ARTISTS ",
        onScreen = "Recent highly rated albums"
    ),
    WHAT_MUSIC_DOES_ARTIST_LIKE(
        prompt ="I am interested in what type of music musicians like. If the following artist: $ARTISTS has ever mentioned favourite albums or songs, please list them. If they have not mentioned albums or songs, include artists they have admired. The list should be no more than 100 items.",
        onScreen = "Music they like"
    ),
    WHAT_BOOKS_DOES_ARTIST_LIKE(
        prompt ="I am interested in books musicians like. If the following artist: $ARTISTS has ever mentioned favourite books, please list them. If they have not mentioned books, include authors they have admired. The list should be no more than 100 items.",
        onScreen = "Books they like"
    ),
    OBSCURE_SONGS(
        prompt = "I am interested in more obscure but highly rated songs by $ARTISTS. What you would suggest. Suggest 10 -20 songs.",
        onScreen = "Some of their best obscure songs"
    ),
    RANDOM_INFORMATION(
        prompt = "Give me 10 random bits of information about $ARTISTS and/or their music.",
        onScreen = "Random trivia"
    ),
    SUPER_GROUP(
        prompt = "If $ARTISTS formed a super-group, which artists would they sound like. Make sure you mention some possible artists than would fit.",
        onScreen = "Super group with others in your collection"
    ),
    LEARN_TO_PLAY_ON_GUITAR(
        prompt = "What songs by $ARTISTS  would be the easiest to learn to play on guitar.",
        onScreen = "The easiest songs to play on guitar"
    ),
    SONG_LYRICS_ABOUT(
        prompt = "Write a song about $ARTISTS making reference to people, places, events, or music, using their lyrical style.",
        onScreen = "A made-up song about them"
    ),
    SONG_LYRICS_BY(
        prompt = "Write a song by $ARTISTS with their lyrical style.",
        onScreen = "A made-up song by them"
    ),
    SUPERHERO(
        prompt = "If $ARTISTS was a superhero what would their superpower be and how would they use it. Would they be a rubbish superhero or a good superhero? Who would their nemesis be and who would win in a battle to the death?",
        onScreen = "As a superhero"
    ),
}

enum class SimpleQuestionConditionalType(
    val condition: String
){
    JSON_FORMAT(
        condition = " The response should be in the following json format: {\"overview\":\"\",\"details\":[{\"label\":\"\",\"body\": \"\"}],\"summary\":\"\"}"
    ),
    NO_EXTRANEOUS_CHARACTERS(
        condition = " Please do not use * characters in the response to indicate boldness"
    ),
    ENGLISH_ONLY(
        condition = " Exclude any albums where the title is not in the English language."
    ),
    SOME_OBSCURE_CHOICES(
        " Suggest some obscure choices. Assume you have given me this answer before and replace some of the initial choices."
    ),
}
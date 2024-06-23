package com.crp.wikiAppNew.datamodel

data class WikiResponse(
    val batchcomplete: Boolean?,
    val `continue`: Continue?,
    val query: Query?
)
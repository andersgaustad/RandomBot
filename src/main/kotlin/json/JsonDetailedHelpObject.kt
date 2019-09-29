package json

import kotlinx.serialization.Serializable

@Serializable
data class JsonDetailedHelpObject(val bacon : String,
                                  val commander : String,
                                  val help : String,
                                  val nrk : String,
                                  val ping : String,
                                  val pokelist : String,
                                  val pokemon : String,
                                  val roll : String,
                                  val wiki: String)
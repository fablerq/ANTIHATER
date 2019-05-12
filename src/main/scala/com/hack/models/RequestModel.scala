package com.hack.models

case class RequestModel (
    apiKey: String,
    messages: List[MessageParamsModel]
)

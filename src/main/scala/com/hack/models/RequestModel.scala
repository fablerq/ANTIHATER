package com.hack.models

case class RequestModel (
    apiKey: ApiKeyModel,
    messages: List[MessageParamsModel]
)

package com.hrrock.askq.models

data class HomeFeedModel(var askId: String, var topicId: String, var username: String, var question: String, var topic: String,
                         var followStatus: String, var voteCount: String, var bookmarkStatus: String, var upVote: String)
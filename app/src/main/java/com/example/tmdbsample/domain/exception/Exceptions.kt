package com.example.tmdbsample.domain.exception

import java.net.UnknownHostException


// retrofit throws UnknownHostException if internet is not connected, use alias so it look prettier
typealias NoInternetException = UnknownHostException
package com.application.app.predict

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader


fun main(args: Array<String>) {
    csvReader().open("/Users/blendicavlad/Downloads/single-family-home-sales.csv") {
        readAllAsSequence().forEach { row ->
            if (!row[0].startsWith(";"))
                println(row[0].substring(8..9))
        }
    }
}


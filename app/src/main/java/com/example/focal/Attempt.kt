package com.example.focal

import androidx.camera.core.ImageProxy
import java.sql.Timestamp

class Attempt(
    timestamp: Timestamp,
    exercise: String,
    feedback: String,
    image: ImageProxy
) : java.io.Serializable{
}
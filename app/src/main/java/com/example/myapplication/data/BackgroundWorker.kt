package com.example.myapplication.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters

class BackgroundWorker(context: Context,
                       workerParams: WorkerParameters,rep:MedicamentRepo
) : CoroutineWorker(context, workerParams) {
   val  r: MedicamentRepo=rep


    override suspend fun doWork(): Result {
       // inputData.
        r.save_online()
       return Result.success()
    }
}
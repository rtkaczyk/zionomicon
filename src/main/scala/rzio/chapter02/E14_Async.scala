package rzio.chapter02

import zio._

// Ex 14-15
object E14_Async:
  def getCacheValue(key: String, onSuccess: String => Unit, onFailure: Throwable => Unit): Unit =
    ???

  def getCacheValueZio(key: String): ZIO[Any, Throwable, String] =
    ZIO.effectAsync(callback =>
      getCacheValue(key, s => callback(ZIO.succeed(s)), f => callback(ZIO.fail(f)))
    )

  trait User

  def saveUserRecord(user: User, onSuccess: () => Unit, onFailure: Throwable => Unit): Unit = ???

  def saveUserRecordZio(user: User): ZIO[Any, Throwable, Unit] =
    ZIO.effectAsync(cb => saveUserRecord(user, () => cb(ZIO.unit), f => cb(ZIO.fail(f))))

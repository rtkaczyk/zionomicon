package rzio.chapter02

// Ex 6-9
object E06_ToyZio:
  final case class ZIO[-R, +E, +A](run: R => Either[E, A])
  type F[R, E, A] = R => Either[E, A]

  def zipWith[R, E, A, B, C](self: ZIO[R, E, A], that: ZIO[R, E, B])(f: (A, B) => C): ZIO[R, E, C] = {
    val z: F[R, E, (A, B)] = r => self.run(r).flatMap(a => that.run(r).map(b => (a, b)))
    val r: F[R, E, C]      = z.andThen(_.map(f.tupled))
    ZIO(r)
  }

  def collectAll[R, E, A](in: Iterable[ZIO[R, E, A]]): ZIO[R, E, List[A]] =
    in.toList match {
      case Nil =>
        ZIO(_ => Right(Nil))
      case x :: xs =>
        val init: F[R, E, List[A]] = x.run.andThen(_.map(List(_)))
        val f: F[R, E, List[A]] =
          r =>
            xs.foldLeft(init(r)) { (acc, z) =>
              acc.flatMap(a => z.run(r).map(_ :: a))
            }

        ZIO(f.andThen(_.map(_.reverse)))
    }

  def foreach[R, E, A, B](in: Iterable[A])(f: A => ZIO[R, E, B]): ZIO[R, E, List[B]] = collectAll(in.map(f))

  def orElse[R, E1, E2, A](self: ZIO[R, E1, A], that: ZIO[R, E2, A]): ZIO[R, E2, A] = ZIO(r =>
    self.run(r).left.flatMap(_ => that.run(r))
  )

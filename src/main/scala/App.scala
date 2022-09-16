package fi.spectrum

import configs.{AppContext, ConfigBundle}
import processes.DownloaderProgram
import repositories.pg.{doobieLogging, PostgresTransactor}
import repositories.{AddressRepository, OrderRepository, StateRepository}
import services.WeightResolver

import cats.effect.{Blocker, Resource}
import fi.spectrum.App.RunF
import tofu.doobie.log.EmbeddableLogHandler
import tofu.doobie.transactor.Txr
import tofu.lift.IsoK
import tofu.logging.Logs
import zio.{ExitCode, URIO, ZIO}
import zio.interop.catz._
import tofu.fs2Instances._

object App extends EnvApp[AppContext] {

  def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    init(args.headOption).use(_ => ZIO.never).orDie

  def init(configPathOpt: Option[String]): Resource[InitF, Unit] =
    for {
      blocker <- Blocker[InitF]
      configs <- Resource.eval(ConfigBundle.load[InitF](configPathOpt, blocker))
      _                                     = println(configPathOpt)
      ctx                                   = AppContext.init(configs)
      implicit0(isoKRun: IsoK[RunF, InitF]) = isoKRunByContext(ctx)
      analyticsTrans  <- PostgresTransactor.make[InitF]("analytics-db-trans", configs.analyticsPg)
      downloaderTrans <- PostgresTransactor.make[InitF]("downloader-db-trans", configs.downloaderPg)
      xaD: Txr.Continuational[RunF]           = Txr.continuational[RunF](downloaderTrans.mapK(wr.liftF))
      implicit0(xa: Txr.Continuational[RunF]) = Txr.continuational[RunF](analyticsTrans.mapK(wr.liftF))
      implicit0(elh: EmbeddableLogHandler[xa.DB]) <- Resource.eval(
                                                       doobieLogging.makeEmbeddableHandler[InitF, RunF, xa.DB](
                                                         "analytics-db-logging"
                                                       )
                                                     )
      implicit0(logsDb: Logs[InitF, xa.DB]) = Logs.sync[InitF, xa.DB]

      implicit0(addresses: AddressRepository[RunF]) <- Resource.eval(AddressRepository.create[InitF, xa.DB, RunF])
      implicit0(orders: OrderRepository[RunF])      <- Resource.eval(OrderRepository.create[InitF, xa.DB, RunF])
      implicit0(state: StateRepository[RunF])       <- Resource.eval(StateRepository.create[InitF, xaD.DB, RunF](xaD))
      implicit0(resolver: WeightResolver[RunF])     <- Resource.eval(WeightResolver.make[InitF, RunF](configs.resolver))
      implicit0(program: DownloaderProgram[StreamF]) <-
        Resource.eval(DownloaderProgram.make[InitF, RunF, StreamF](configs.resolver))
      _ <- Resource.eval(program.run.compile.drain).mapK(isoKRun.tof)
    } yield ()
}

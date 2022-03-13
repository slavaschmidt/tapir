package sttp.tapir.server.interceptor.decodefailure

import sttp.monad.MonadError
import sttp.monad.syntax._
import sttp.tapir.model.ServerResponse
import sttp.tapir.server.interceptor._
import sttp.tapir.server.interpreter.BodyListener

class DecodeFailureInterceptor[F[_]](handler: DecodeFailureHandler) extends EndpointInterceptor[F] {
  override def apply[B](responder: Responder[F, B], endpointHandler: EndpointHandler[F, B]): EndpointHandler[F, B] =
    new EndpointHandler[F, B] {
      override def onDecodeSuccess[U, I](
          ctx: DecodeSuccessContext[F, U, I]
      )(implicit monad: MonadError[F], bodyListener: BodyListener[F, B]): F[ServerResponse[B]] =
        endpointHandler.onDecodeSuccess(ctx)

      override def onSecurityFailure[A](
          ctx: SecurityFailureContext[F, A]
      )(implicit monad: MonadError[F], bodyListener: BodyListener[F, B]): F[ServerResponse[B]] =
        endpointHandler.onSecurityFailure(ctx)

      override def onDecodeFailure(
          ctx: DecodeFailureContext
      )(implicit monad: MonadError[F], bodyListener: BodyListener[F, B]): F[Option[ServerResponse[B]]] = {
        handler(ctx) match {
          case None               => endpointHandler.onDecodeFailure(ctx)
          case Some(valuedOutput) => responder(ctx.request, valuedOutput).map(Some(_))
        }
      }
    }
}

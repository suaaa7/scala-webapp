package infrastructure.actor

import akka.actor.actor
import com.google.inject.inject
import domain.repository.PicturePropertyRepository
import play.api.Configuration

sealed abstruct trait ConvertPictureActorMessage

case object ConvertPictureMessage extends ConvertPictureActorMessage

class ConvertPictureActor @Inject()(
  redisClient: RedisClient,
  picturePropertyRepository: PicturePropertyRepository
  configuration: Configuration
) extends Actor {

  override def receive = {
    case ConvertPictureMessage => {
      // TODO 画像変換処理の実装
      println("画像変換処理を実行")
    }
  }
}
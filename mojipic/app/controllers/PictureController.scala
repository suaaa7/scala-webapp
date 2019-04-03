package controllers

import java.nio.file.{FileSystems, Files, Path, StandardCopyOption}
import java.time.{Clock, LocalDateTime}
import javax.inject.{Inject, Singleton}

import com.google.common.net.MediaType
import com.redis.RedisClient
import domain.entity.PictureProperty
import domain.repository.PicturePropertyRepository
import infrastructure.redis.RedisKeys
import play.api.cache._
import play.api.libs.Files.TemporaryFile
import play.api.mvc._
import play.api.mvc.MultipartFormData.FilePart

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PicturesController @Inject()(
  cc: ControllerComponents,
  clock: Clock,
  executionContext: ExecutionContext,
  val cache: SyncCacheApi,
  picturePropertyRepository: PicturePropertyRepository,
  redisClient: RedisClient
) extends AbstractController(cc) {

  implicit val ec = executionContext
  val originalStoreDirPath = "./fileSystem/original"

  def post = Action.async { request =>
    request.body.asMultipartFormData match {
      case Some(form) =>
        form.file("file") match {
          case Some(file) =>
            val storeDirPath = FileSystems.getDefault.getPath(originalStoreDirPath)
            if (!Files.exists(storeDirPath)) Files.createDirectories(storeDirPath)

            val cTM = System.currentTimeMillis().toString
            val originalFilepath = FileSystems.getDefault.getPath(storeDirPath.toString, cTM)
            Files.copy(file.ref.path, originalFilepath, StandardCopyOption.COPY_ATTRIBUTES)
            val propertyValue = createPicturePropertyValue(file, form, originalFilepath)

            val pictureId = picturePropertyRepository.create(propertyValue)
            pictureId.map({ (id) =>
              redisClient.rpush(RedisKeys.Tasks, id.value)
              Ok("Picture uploades.")
            })

            Future.successful(Ok("Picture uploaded."))
          case _ => Future.successful(Unauthorized("Need picture data."))
        }
      case _ => Future.successful(Unauthorized("Need picture data.")) 
    }
  }

  private[this] def createPicturePropertyValue(
    file: FilePart[TemporaryFile],
    form: MultipartFormData[TemporaryFile],
    originalFilePath: Path
  ): PictureProperty.Value = {
    val overlayText = form.dataParts.get("overlaytext").flatMap(_.headOption).getOrElse("")
    val overlayTextSize = form.dataParts.get("overlaytextsize").flatMap(_.headOption).getOrElse("60").toInt
    val contentType = MediaType.parse(file.contentType.getOrElse("application/octet-stream"))

    PictureProperty.Value(
      PictureProperty.Status.Converting,
      file.filename,
      contentType,
      overlayText,
      overlayTextSize,
      Some(originalFilePath.toString),
      None,
      LocalDateTime.now(clock))
  }
}
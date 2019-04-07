package infrastructure.repository

import domain.entity.{PictureId, PictureProperty}
import com.google.common.net.MediaType
import domain.entity.{PictureId, PictureProperty, TwitterId}
import domain.repository.PicturePropertyRepository
import scalikejdbc._

import scala.concurrent.Future
import scala.util.Try

class PicturePropertyRepositoryImpl extends PicturePropertyRepository {

  def create(value: PictureProperty.Value): Future[PictureId] =
    Future.fromTry(Try {
      using(DB(ConnectionPool.borrow())) { db =>
        db.localTx { implicit session =>
          val sql =
            sql"""INSERT INTO picture_properties (
                 | status,
                 | file_name,
                 | content_type,
                 | overlay_text,
                 | overlay_text_size,
                 | original_filepath,
                 | converted_filepath,
                 | created_time
                 | ) VALUES (
                 | ${value.status.value},
                 | ${value.fileName},
                 | ${value.contentType.toString},
                 | ${value.overlayText},
                 | ${value.overlayTextSize},
                 | ${value.originalFilepath.getOrElse(null)},
                 | ${value.convertedFilepath.getOrElse(null)},
                 | ${value.createdTime}
                 | )
              """.stripMargin
          PictureId(sql.updateAndReturnGeneratedKey().apply())
        }
      }
    })
  
  def find(pictureId: PictureId): Future[PictureProperty] =
    Future.fromTry(Try {
      using(DB(ConnectionPool.borrow())) { db =>
        db.readOnly { implicit session =>
          val sql =
            sql"""SELECT
                 | picture_id,
                 | status,
                 | file_name,
                 | content_type,
                 | overlay_text,
                 | overlay_text_size,
                 | originam_filepath,
                 | converted_filepath,
                 | created_time
                 | FROM picture_properties WHERE picture_id = ${pictureId.value}
            """.stripMargin
          sql.map(resultSetToPictureProperty).single().apply()
            .getOrElse(throw new RuntimeException(s"Picture is notfound. PcitureId: ${pictureId.value}"))
        }
      }
    })

  private[this] def resultSetToPictureProperty(rs: WrappedResultSet): PictureProperty = {
    val value =
      PictureProperty.Value(
        PictureProperty.Status.parse(rs.string("status")).get,
        rs.string("file_name"),
        MediaType.parse(rs.string("content_type")),
        rs.string("overlay_text"),
        rs.int("overlay_text_size"),
        rs.stringOpt("original_filepath"),
        rs.stringOpt("converted_filepath"),
        rs.localDateTime("created_time")
      )
    PictureProperty(PictureId(rs.long("picture_id")), value)
  }
}
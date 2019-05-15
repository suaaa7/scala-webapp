package domain.repository

import java.time.LocalDateTime

import domain.entity.{PictureId, PictureProperty}

import scala.concurrent.Future

trait PicturePropertyRepository {
  def create(value: PictureProperty.Value): Future[PictureId]

  def find(pictureId: PictureId): Future[PictureProperty]

  def update(pictureId: PictureId, value: PictureProperty.Value): Future[Unit]

  def findAllByDateTime(lastCreatedTime: LocalDateTime): Future[Seq[PictureProperty]]

}
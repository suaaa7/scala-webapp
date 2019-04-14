package domain.repository

import domain.entity.{PictureId, PictureProperty}

import scala.concurrent.Future

trait PicturePropertyRepository {
  def create(value: PictureProperty.Value): Future[PictureId]

  def find(pictureId: PictureId): Future[PictureProperty]

  def update(pictureId: PictureId, value: PictureProperty.Value): Future[Unit]

}
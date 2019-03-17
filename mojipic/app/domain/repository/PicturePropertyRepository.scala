package domain.repository

import domain.entity.{PictureId, PictureProperty}

import scala.concurrent.Future

trait PicturePropertyRepository {
  def create(value: PictureProperty.Value): Future[PictureId]

}

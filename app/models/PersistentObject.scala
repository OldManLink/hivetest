package models

trait PersistentObject
{
  val NEW_OBJECT_ID = -1
  def id: Long

  def isNew: Boolean = id == NEW_OBJECT_ID
}

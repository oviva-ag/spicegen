package com.oviva.spicegen.api;

import java.util.Objects;

public final class UpdateRelationship {
  private final ObjectRef resource;

  private final String relation;

  private final SubjectRef subject;

  private final Operation operation;

  private UpdateRelationship(
      ObjectRef resource, String relation, SubjectRef subject, Operation operation) {
    this.resource = resource;
    this.relation = relation;
    this.subject = subject;
    this.operation = operation;
  }

  public static UpdateRelationship ofUpdate(
      ObjectRef resource, String relation, ObjectRef subject) {
    return new UpdateRelationship(
        resource, relation, SubjectRef.ofObject(subject), Operation.UPDATE);
  }

  public static UpdateRelationship ofDelete(
      ObjectRef resource, String relation, ObjectRef subject) {
    return new UpdateRelationship(
        resource, relation, SubjectRef.ofObject(subject), Operation.DELETE);
  }

  public SubjectRef subject() {
    return subject;
  }

  public ObjectRef resource() {
    return resource;
  }

  public String relation() {
    return relation;
  }

  public Operation operation() {
    return operation;
  }

  @Override
  public String toString() {
    var res = resource != null ? resource.toString() : "";
    var rel = relation != null ? relation : "";
    var sub = subject != null ? subject.toString() : "";
    return operation.name() + "(" + res + "#" + rel + "@" + sub + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    var that = (UpdateRelationship) o;
    return Objects.equals(resource, that.resource)
        && Objects.equals(relation, that.relation)
        && Objects.equals(subject, that.subject)
        && operation == that.operation;
  }

  @Override
  public int hashCode() {
    return Objects.hash(resource, relation, subject, operation);
  }

  public enum Operation {
    UPDATE,

    DELETE
  }
}

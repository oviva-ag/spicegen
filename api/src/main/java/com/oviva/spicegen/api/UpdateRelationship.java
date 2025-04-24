package com.oviva.spicegen.api;

import com.oviva.spicegen.api.internal.UpdateRelationshipImpl;

public interface UpdateRelationship {

  static UpdateRelationship ofUpdate(ObjectRef resource, String relation, ObjectRef subject) {
    return new UpdateRelationshipImpl(
        resource, relation, SubjectRef.ofObject(subject), Operation.UPDATE);
  }

  static UpdateRelationship ofUpdate(ObjectRef resource, String relation, SubjectRef subject) {
    return new UpdateRelationshipImpl(resource, relation, subject, Operation.UPDATE);
  }

  static UpdateRelationship ofDelete(ObjectRef resource, String relation, ObjectRef subject) {
    return new UpdateRelationshipImpl(
        resource, relation, SubjectRef.ofObject(subject), Operation.DELETE);
  }

  static UpdateRelationship ofDelete(ObjectRef resource, String relation, SubjectRef subject) {
    return new UpdateRelationshipImpl(resource, relation, subject, Operation.DELETE);
  }

  SubjectRef subject();

  ObjectRef resource();

  String relation();

  Operation operation();

  enum Operation {
    UPDATE,

    DELETE
  }
}

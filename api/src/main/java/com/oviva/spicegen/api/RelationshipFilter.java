package com.oviva.spicegen.api;

import com.oviva.spicegen.api.internal.RelationshipFilterImpl;
import java.util.Optional;

public interface RelationshipFilter {

  static Builder newBuilder() {
    return new RelationshipFilterImpl.Builder();
  }

  String resourceKind();

  Optional<String> resourceId();

  Optional<String> relation();

  Optional<SubjectFilter> subjectFilter();

  interface SubjectFilter {

    static Builder newBuilder() {
      return new RelationshipFilterImpl.SubjectFilterImpl.Builder();
    }

    String subjectKind();

    Optional<String> subjectId();

    Optional<String> relation();

    interface Builder {

      Builder subjectKind(String val);

      Builder subjectId(String val);

      Builder relation(String val);

      SubjectFilter build();
    }
  }

  interface Builder {

    Builder resourceKind(String val);

    Builder resourceId(String val);

    Builder relation(String val);

    Builder subjectFilter(SubjectFilter val);

    RelationshipFilter build();
  }
}

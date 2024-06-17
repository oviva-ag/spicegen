package com.oviva.spicegen.api.internal;

import com.oviva.spicegen.api.RelationshipFilter;
import java.util.Optional;

public record RelationshipFilterImpl(
    String resourceKind,
    Optional<String> resourceId,
    Optional<String> relation,
    Optional<RelationshipFilter.SubjectFilter> subjectFilter)
    implements RelationshipFilter {

  private RelationshipFilterImpl(Builder builder) {
    this(
        builder.resourceKind,
        Optional.ofNullable(builder.resourceId),
        Optional.ofNullable(builder.relation),
        Optional.ofNullable(builder.subjectFilter));
  }

  public record SubjectFilterImpl(
      String subjectKind, Optional<String> subjectId, Optional<String> relation)
      implements SubjectFilter {

    private SubjectFilterImpl(Builder builder) {
      this(
          builder.subjectKind,
          Optional.ofNullable(builder.subjectId),
          Optional.ofNullable(builder.relation));
    }

    public static final class Builder implements SubjectFilter.Builder {
      private String subjectKind;
      private String subjectId;
      private String relation;

      public Builder subjectKind(String val) {
        subjectKind = val;
        return this;
      }

      public Builder subjectId(String val) {
        subjectId = val;
        return this;
      }

      public Builder relation(String val) {
        relation = val;
        return this;
      }

      public SubjectFilterImpl build() {
        return new SubjectFilterImpl(this);
      }
    }
  }

  public static final class Builder implements RelationshipFilter.Builder {
    private String resourceKind;
    private String resourceId;
    private String relation;
    private SubjectFilter subjectFilter;

    @Override
    public Builder resourceKind(String val) {
      resourceKind = val;
      return this;
    }

    @Override
    public Builder resourceId(String val) {
      resourceId = val;
      return this;
    }

    @Override
    public Builder relation(String val) {
      relation = val;
      return this;
    }

    @Override
    public Builder subjectFilter(SubjectFilter val) {
      this.subjectFilter = val;
      return this;
    }

    public RelationshipFilterImpl build() {
      return new RelationshipFilterImpl(this);
    }
  }
}

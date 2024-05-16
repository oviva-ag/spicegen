// package com.oviva.spicegen.generator.internal;
//
//
// import com.oviva.spicegen.generator.SubjectRef;
// import com.oviva.spicegen.refs.ObjectRef;
//
// import java.util.UUID;
//
// public class UpdateCoachRelationshipsBuilder {
//
//  private Map<String, Tuple<ObjectRef, SubjectRef>> chosenRelations = new HashMap<String,
// Tuple<ObjectRef, SubjectRef>>();
//  private final Operation operation;
//  public UpdateCoachRelationshipsBuilder(Operation operation){
//    this.operation = operation;
//  }
//
//  public UpdateCoachRelationshipsBuilder withCoachTenant(UUID coachId, UUID tenantId){
//    if (!chosenRelations.containsKey(Constants.RELATIONSHIP_COACH_TENANT)){
//      chosenRelations.put(Constants.RELATIONSHIP_COACH_TENANT, new
// Tuple(ObjectRef.ofUuid(coachId), SubjectRef.ofUuid(tenantId)));
//    }
//  }
//
//  public UpdateRelationships.UpdateRelationshipsBuilder build(){
//    var builder = UpdateRelationships.UpdateRelationshipsBuilder.newBuilder();
//    switch (operation){
//      case DELETE:
//        for(String relation: chosenRelations.keys()){
//        builder.addUpdate(
//                UpdateRelationship.ofDelete(
//                        chosenRelations.get(relation).first(),
//                        relation,
//                        chosenRelations.get(relation).second()));
//        }
//        break;
//      case UPDATE:
//        for(String relation: chosenRelations.keys()){
//          builder.addUpdate(
//                  UpdateRelationship.ofUpdate(
//                          chosenRelations.get(relation).first(),
//                          relation,
//                          chosenRelations.get(relation).second()));
//        }
//      }
//    return builder;
//    }
//  }
//
//  public enum Operation{
//    UPDATE, DELETE
//  }
// }

package at.ac.htlleonding.boundary;

import at.ac.htlleonding.dto.ErrorResponse;
import at.ac.htlleonding.dto.TeamDTO;
import at.ac.htlleonding.model.Team;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;

@Path("/api/teams")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeamResource {

    @Context
    UriInfo uriInfo;

    @GET
    public Response getAllTeams() {
        try {
            List<TeamDTO> teams = Team.streamAll()
                .map(TeamDTO::from)
                .toList();
            
            return Response.ok(teams).build();
        } catch (Exception e) {
            return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ErrorResponse.of(500, "Fehler beim Laden der Teams", uriInfo.getPath()))
                .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getTeam(@PathParam("id") Long id) {
        Team team = Team.findById(id);
        
        if (team == null) {
            return Response
                .status(Response.Status.NOT_FOUND)
                .entity(ErrorResponse.of(404, "Team mit ID " + id + " nicht gefunden", uriInfo.getPath()))
                .build();
        }
        
        return Response.ok(TeamDTO.from(team)).build();
    }

    @POST
    @Transactional
    public Response createTeam(@Valid TeamDTO dto) {
        try {
            // Validierung: Position darf nicht negativ sein
            if (dto.position() < 0) {
                return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ErrorResponse.of(400, "Position darf nicht negativ sein", uriInfo.getPath()))
                    .build();
            }

            // Prüfen ob Position bereits existiert
            Team existing = Team.findByPosition(dto.position());
            if (existing != null) {
                return Response
                    .status(Response.Status.CONFLICT)
                    .entity(ErrorResponse.of(409, "Team mit Position " + dto.position() + " existiert bereits", uriInfo.getPath()))
                    .build();
            }

            Team team = dto.toEntity();
            team.persist();

            URI location = uriInfo.getAbsolutePathBuilder().path(team.id.toString()).build();
            return Response
                .created(location)
                .entity(TeamDTO.from(team))
                .build();

        } catch (Exception e) {
            return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ErrorResponse.of(500, "Fehler beim Erstellen des Teams: " + e.getMessage(), uriInfo.getPath()))
                .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateTeam(@PathParam("id") Long id, @Valid TeamDTO dto) {
        Team team = Team.findById(id);
        
        if (team == null) {
            return Response
                .status(Response.Status.NOT_FOUND)
                .entity(ErrorResponse.of(404, "Team mit ID " + id + " nicht gefunden", uriInfo.getPath()))
                .build();
        }

        try {
            team.position = dto.position();
            team.persist();
            
            return Response.ok(TeamDTO.from(team)).build();
        } catch (Exception e) {
            return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ErrorResponse.of(500, "Fehler beim Aktualisieren des Teams: " + e.getMessage(), uriInfo.getPath()))
                .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteTeam(@PathParam("id") Long id) {
        Team team = Team.findById(id);
        
        if (team == null) {
            return Response
                .status(Response.Status.NOT_FOUND)
                .entity(ErrorResponse.of(404, "Team mit ID " + id + " nicht gefunden", uriInfo.getPath()))
                .build();
        }

        try {
            team.delete();
            return Response.noContent().build();
        } catch (Exception e) {
            return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ErrorResponse.of(500, "Fehler beim Löschen des Teams: " + e.getMessage(), uriInfo.getPath()))
                .build();
        }
    }
}

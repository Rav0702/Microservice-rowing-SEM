package nl.tudelft.sem.template.controllers;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.services.EventService;
import nl.tudelft.sem.template.shared.domain.Position;
import nl.tudelft.sem.template.shared.entities.Event;
import nl.tudelft.sem.template.shared.entities.EventModel;
import nl.tudelft.sem.template.shared.entities.User;
<<<<<<< event-microservice/src/main/java/nl/tudelft/sem/template/controllers/EventController.java
=======
import nl.tudelft.sem.template.shared.enums.Certificate;
import nl.tudelft.sem.template.shared.enums.EventType;
>>>>>>> event-microservice/src/main/java/nl/tudelft/sem/template/controllers/EventController.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
<<<<<<< event-microservice/src/main/java/nl/tudelft/sem/template/controllers/EventController.java
import reactor.core.publisher.Mono;
=======
>>>>>>> event-microservice/src/main/java/nl/tudelft/sem/template/controllers/EventController.java

@RestController
public class EventController {
    private final transient EventService eventService;
    private static WebClient client;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Get all events.
     *
     * @return List of events
     */
    @GetMapping("/all")
    public List<Event> getEvents() {
        return eventService.getAllEvents();
    }

    /** matches suitable events with a user.
     *
     * @param user the user to match the events to
     * @return the events that match the user
     * @throws IllegalArgumentException if the user profile is not full
     */
    @GetMapping("/matchEvents")
    public List<Event> matchEvents(@RequestBody User user) throws IllegalArgumentException {
        if (user.getCertificate() == null || user.getPositions() == null || user.getPositions().size() == 0
                || user.getCertificate() == null || user.getOrganization() == null) {
            throw new IllegalArgumentException("Profile is not (fully) completed");
        }
        return eventService.getMatchedEvents(user);
    }

    /**
     * POST API to register an event to the platform.
     *
     * @param eventModel a dummy-like event object
     * @return the newly added object
     * @throws Exception if something goes wrong
     */
    @PostMapping("/register")
    public ResponseEntity<Event> registerNewEvent(@RequestBody EventModel eventModel) throws Exception {
        try {
            Event event = new Event(eventModel.getOwningUser(),
                    eventModel.getLabel(),
                    eventModel.getPositions(),
                    eventModel.getStartTime(),
                    eventModel.getEndTime(),
                    eventModel.getCertificate(),
                    eventModel.getType(),
                    eventModel.getOrganisation());
            Event receivedEvent = eventService.insert(event);
            return ResponseEntity.ok(receivedEvent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DELETE API to delete an event from the platform.
     *
     * @param eventId the event's id
     * @return an ok message if it goes right
     */
    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable("eventId") Long eventId) {
        try {
            eventService.deleteById(eventId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * PUT API to update an already existent event.
     *
     * @param eventId the event's id
     * @param eventModel a dummy-like event object
     * @param editCompetition whether we need to also update the level of competitiveness
     * @return the newly updated event
     */
    @PutMapping("/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable("eventId") Long eventId,
                                          @RequestBody EventModel eventModel,
                                          @RequestParam("editCompetition") boolean editCompetition) {
        Optional<Event> returned = eventService.updateById(eventModel.getOwningUser(), eventId, eventModel.getLabel(),
            eventModel.getPositions(), eventModel.getStartTime(), eventModel.getEndTime(), eventModel.getCertificate(),
            eventModel.isCompetitive(), eventModel.getType(), eventModel.getOrganisation(), editCompetition);

        if (returned.isEmpty()) {
            return ResponseEntity.badRequest().build();
        } else {
    
    /**
     * Edit an event by id.
     *
     * @param eventId id of the event to edit
     * @return response entity
     */
    @PutMapping("edit/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable("eventId") Long eventId,
                                         @RequestBody EventModel eventModel) {
        Optional<Event> returned = eventService.updateById(eventModel.getOwningUser(), eventId, eventModel.getLabel(),
                eventModel.getPositions(), eventModel.getStartTime(), eventModel.getEndTime(),
                eventModel.getCertificate(), eventModel.getType(), eventModel.getOrganisation());
        if (!returned.isPresent()) {
            return ResponseEntity.ok(returned.get());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT API for enqueueing a user to an event.
     *
     * @param eventId the id of the event
     * @param userId the id of the user
     * @return "NOT_FOUND" if the ids don't match something or "ENQUEUED" if task gets completed
     */
    @PutMapping("/enqueue/{eventId}/")
    public ResponseEntity<String> enqueue(@PathVariable("eventId") Long eventId, @RequestParam("userId") Long userId) {
        Optional<Event> event = eventService.getById(eventId);
        if (event.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        //Getting the User info from the database
        this.client = WebClient.create();
        Mono<User> response = client.get().uri("http://localhost:8084/api/user/" + userId)
            .retrieve().bodyToMono(User.class).log();
        User user = response.block();
        event.get().enqueue(user);
        return ResponseEntity.ok("ENQUEUED");
    }
}

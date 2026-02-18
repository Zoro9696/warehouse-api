package com.fulfilment.application.monolith.location;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LocationGatewayTest {

  @Test
  public void testWhenResolveExistingLocationShouldReturn() {
    // given
    // LocationGateway locationGateway = new LocationGateway();

    // when
    // Location location = locationGateway.resolveByIdentifier("ZWOLLE-001");

    // then
    // assertEquals(location.identification, "ZWOLLE-001");

  }
  private LocationGateway locationGateway;

  @BeforeEach
  void setUp() {
    locationGateway = new LocationGateway();
  }

  @Test
  void testResolveExistingLocation() {
    Location location = locationGateway.resolveByIdentifier("ZWOLLE-001");

    assertNotNull(location);
    assertEquals("ZWOLLE-001", location.getIdentification());
  }

  @Test
  void testResolveExistingLocationCaseInsensitive() {
    Location location = locationGateway.resolveByIdentifier("zwolle-001");

    assertNotNull(location);
    assertEquals("ZWOLLE-001", location.getIdentification());
  }

  @Test
  void testResolveWithNullIdentifierShouldThrowException() {
    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> locationGateway.resolveByIdentifier(null)
    );

    assertEquals("Location identifier must not be null or blank", exception.getMessage());
  }

  @Test
  void testResolveWithBlankIdentifierShouldThrowException() {
    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> locationGateway.resolveByIdentifier(" ")
    );

    assertEquals("Location identifier must not be null or blank", exception.getMessage());
  }

  @Test
  void testResolveNonExistingLocationShouldThrowException() {
    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> locationGateway.resolveByIdentifier("UNKNOWN-001")
    );

    assertTrue(exception.getMessage().contains("Location not found"));
  }

}
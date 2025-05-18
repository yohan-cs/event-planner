package com.yohan.event_planner.exception;

/**
 * Exception thrown when a user resource cannot be found
 * by either user ID or username.
 */
public class UserNotFoundException extends ResourceNotFoundException {

  /**
   * Constructs a UserNotFoundException for a user ID.
   *
   * @param id the user ID that was not found
   */
  public UserNotFoundException(Long id) {
    super("User with ID " + id + " not found");
  }

  /**
   * Constructs a UserNotFoundException for a username.
   *
   * @param username the username that was not found
   */
  public UserNotFoundException(String username) {
    super("User with username " + username + " not found");
  }
}

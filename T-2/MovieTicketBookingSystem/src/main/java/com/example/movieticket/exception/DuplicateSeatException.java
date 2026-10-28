package com.example.movieticket.exception;
public class DuplicateSeatException extends RuntimeException {
    public DuplicateSeatException(String seat, String screening) { super("Seat " + seat + " is already booked for screening " + screening); }
}

package com.example.dental.repository;

import java.util.List;
import java.sql.Timestamp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.dental.model.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, String> {

    List<Appointment> findByUserId(String userId);

    Page<Appointment> findByItemIdAndDoctorIdAndUserId(String itemId, String doctorId, String userId,
            Pageable pageable);

    Page<Appointment> findByItemIdAndDoctorId(String itemId, String doctorId, Pageable pageable);

    Page<Appointment> findByItemIdAndUserId(String itemId, String userId, Pageable pageable);

    Page<Appointment> findByDoctorIdAndUserId(String doctorId, String userId, Pageable pageable);

    Page<Appointment> findByItemId(String itemId, Pageable pageable);

    Page<Appointment> findByDoctorId(String doctorId, Pageable pageable);

    Page<Appointment> findByUserId(String userId, Pageable pageable);

    Appointment findByAppointmentId(String appointmentId);

    List<Appointment> findByAppointmentTimeBetween(Timestamp start, Timestamp end);
    
}

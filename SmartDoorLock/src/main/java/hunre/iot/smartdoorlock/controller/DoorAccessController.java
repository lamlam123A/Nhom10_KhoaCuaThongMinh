package hunre.iot.smartdoorlock.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hunre.iot.smartdoorlock.dto.DoorControlRequest;
import hunre.iot.smartdoorlock.entity.DefaultKey;
import hunre.iot.smartdoorlock.entity.DoorAccessLog;
import hunre.iot.smartdoorlock.entity.ValidCardId;
import hunre.iot.smartdoorlock.service.DoorAccessService;

@RestController
@RequestMapping("/api")
public class DoorAccessController {

	@Autowired
	private DoorAccessService service;

	// Get all logs
	@GetMapping("/logs")
	public List<DoorAccessLog> getAllLogs() {
		return service.getAllLogs();
	}

	// Get log by ID
	@GetMapping("/logs/{id}")
	public ResponseEntity<DoorAccessLog> getLogById(@PathVariable Long id) {
		return service.getLogById(id).map(log -> ResponseEntity.ok(log)).orElse(ResponseEntity.notFound().build());
	}

	// Add new log
	@PostMapping("/logs")
	public DoorAccessLog addLog(@RequestBody DoorAccessLog log) {
		return service.addLog(log);
	}

	// Delete log by ID
	@DeleteMapping("/logs/{id}")
	public ResponseEntity<Void> deleteLog(@PathVariable Long id) {
		service.deleteLog(id);
		return ResponseEntity.ok().build();
	}

	// Update default key
	@PutMapping("/default_key")
	public DefaultKey updateDefaultKey(@RequestBody DefaultKey defaultKey) {
		return service.updateDefaultKey(defaultKey);
	}

	// Update valid card ID
	@PutMapping("/valid_card_id")
	public ValidCardId updateValidCardId(@RequestBody ValidCardId validCardId) {
		return service.updateValidCardId(validCardId);
	}

	// Get default key
	@GetMapping("/default_key")
	public ResponseEntity<DefaultKey> getDefaultKey() {
		Optional<DefaultKey> defaultKey = service.getDefaultKey();
		return defaultKey.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	// Get valid card ID
	@GetMapping("/valid_card_id")
	public ResponseEntity<ValidCardId> getValidCardId() {
		Optional<ValidCardId> validCardId = service.getValidCardId();
		return validCardId.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping("/control-door")
	public ResponseEntity<Map<String, String>> controlDoor(@RequestBody DoorControlRequest request) {
		boolean success = service.controlDoor(request.isOpen());
		
		System.out.println("bool: " + success);
		System.out.println("Request isOpen: " + request.isOpen());

		
		Map<String, String> response = new HashMap<>();

		if (success) {
			if (request.isOpen()) {
				response.put("message", "Door opened successfully");
			} else {
				response.put("message", "Door closed successfully");
			}
			return ResponseEntity.ok(response);
		} else {
			response.put("message", "Failed to control door");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

}

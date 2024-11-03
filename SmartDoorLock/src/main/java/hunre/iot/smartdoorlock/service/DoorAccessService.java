package hunre.iot.smartdoorlock.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hunre.iot.smartdoorlock.entity.DefaultKey;
import hunre.iot.smartdoorlock.entity.DoorAccessLog;
import hunre.iot.smartdoorlock.entity.ValidCardId;
import hunre.iot.smartdoorlock.repository.DefaultKeyRepository;
import hunre.iot.smartdoorlock.repository.DoorAccessLogRepository;
import hunre.iot.smartdoorlock.repository.ValidCardIdRepository;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;


@Service
public class DoorAccessService {

	@Autowired
	private DoorAccessLogRepository logRepository;

	@Autowired
	private DefaultKeyRepository defaultKeyRepository;

	@Autowired
	private ValidCardIdRepository validCardIdRepository;

	public List<DoorAccessLog> getAllLogs() {
		return logRepository.findAll();
	}

	public Optional<DoorAccessLog> getLogById(Long id) {
		return logRepository.findById(id);
	}

	public DoorAccessLog addLog(DoorAccessLog log) {
		return logRepository.save(log);
	}

	public void deleteLog(Long id) {
		logRepository.deleteById(id);
	}

	public DefaultKey updateDefaultKey(DefaultKey defaultKey) {
		return defaultKeyRepository.save(defaultKey);
	}

	public ValidCardId updateValidCardId(ValidCardId validCardId) {
		return validCardIdRepository.save(validCardId);
	}

	public Optional<DefaultKey> getDefaultKey() {
		return defaultKeyRepository.findById(1);
	}

	public Optional<ValidCardId> getValidCardId() {
		return validCardIdRepository.findById(1);
	}

	public boolean controlDoor(boolean isOpen) {
	    try {
	        // Thay đổi URL này thành địa chỉ IP của ESP8266
//	        URL url = new URL("http://192.168.136.110/control-door"); // ip esp8266
	    	URL url = new URL("http://172.20.10.4/control-door"); // ip esp8266	
	        HttpURLConnection con = (HttpURLConnection) url.openConnection();
	        con.setRequestMethod("POST");
	        con.setRequestProperty("Content-Type", "application/json");
	        con.setDoOutput(true);
	        try(OutputStream os = con.getOutputStream()) {
	            String jsonInputString = "{\"isOpen\": " + isOpen + "}";
	            byte[] input = jsonInputString.getBytes("utf-8");
	            os.write(input, 0, input.length);           
	        }
	        int responseCode = con.getResponseCode();
	        return responseCode == HttpURLConnection.HTTP_OK;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
}

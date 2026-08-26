package lu.kbra.school_lu.endpoints;

import java.lang.management.ManagementFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

	@RequestMapping("/status")
	public ResponseEntity<Long> status() {
		return ResponseEntity.ok(ManagementFactory.getRuntimeMXBean().getUptime());
	}

}

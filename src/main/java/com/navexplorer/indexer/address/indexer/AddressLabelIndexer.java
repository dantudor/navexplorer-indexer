package com.navexplorer.indexer.address.indexer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navexplorer.library.address.entity.Address;
import com.navexplorer.library.address.repository.AddressRepository;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class AddressLabelIndexer {
    private static final Logger logger = LoggerFactory.getLogger(AddressLabelIndexer.class);

    @Autowired
    private AddressRepository addressRepository;

    public void indexAddressLabels() {
        File labelsFile = new File("/data/addressLabels.json");

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<AddressLabel> addressLabels = mapper.readValue(labelsFile, new TypeReference<List<AddressLabel>>(){});

            addressLabels.forEach(a -> {
                logger.info("Applying {} label to {}", a.getLabel(), a.getAddress());

                Address address = addressRepository.findByHash(a.getAddress());
                if (address != null) {
                    if (address.getLabel() != a.getLabel()) {
                        address.setLabel(a.getLabel());
                        addressRepository.save(address);
                    }
                }
            });
        } catch (IOException e) {
            logger.error("Could not read " + labelsFile.getAbsoluteFile());
        }
    }

    @Data
    private static class AddressLabel {
        private String address;
        private String label;
    }
}

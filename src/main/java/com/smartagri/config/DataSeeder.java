package com.smartagri.config;

import com.smartagri.entity.Advisory;
import com.smartagri.entity.Role;
import com.smartagri.entity.User;
import com.smartagri.repository.AdvisoryRepository;
import com.smartagri.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;

/** Creates the default admin account and starter advisory content on first run. */
@Component
public class DataSeeder implements CommandLineRunner {
    private final UserRepository users;
    private final AdvisoryRepository advisory;
    private final PasswordEncoder encoder;

    public DataSeeder(UserRepository users, AdvisoryRepository advisory, PasswordEncoder encoder) {
        this.users = users; this.advisory = advisory; this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (!users.existsByEmail("admin@smartagri.com")) {
            User a = new User();
            a.setName("Administrator");
            a.setEmail("admin@smartagri.com");
            a.setPassword(encoder.encode("admin123"));
            a.setRole(Role.ADMIN);
            users.save(a);
        }
        if (advisory.count() > 0) return;
        advisory.saveAll(List.of(
            // ---- Irrigation (water schedule & requirement) ----
            new Advisory("IRRIGATION", "Rice", "Water schedule for paddy",
                "Keep 2-5 cm standing water from transplanting to the dough stage. Drain the field 10-15 days before harvest. Total water requirement is about 1200-1500 mm. Alternate wetting and drying can save 20-25% water."),
            new Advisory("IRRIGATION", "Wheat", "Critical irrigation stages",
                "Give 4-6 irrigations: crown root initiation (20-25 days), tillering, jointing, flowering and milk/dough stage. Crown root initiation is the most critical. Requirement is about 450-650 mm."),
            new Advisory("IRRIGATION", "Cotton", "Irrigation for cotton",
                "Irrigate every 10-15 days depending on soil. Flowering and boll formation are the critical stages. Avoid waterlogging. Drip irrigation can save up to 40% water. Requirement is about 700-1300 mm."),
            new Advisory("IRRIGATION", "Tomato", "Irrigation for tomato",
                "Irrigate every 3-5 days in summer and 7-10 days in winter. Keep moisture even to avoid blossom end rot and fruit cracking. Drip irrigation is strongly recommended."),
            new Advisory("IRRIGATION", "Maize", "Irrigation for maize",
                "Irrigate at knee-high, tasseling and silking stages. Water stress at silking can reduce yield sharply. Requirement is about 500-800 mm."),
            // ---- Fertilizer (based on crop & soil) ----
            new Advisory("FERTILIZER", "Rice", "Fertilizer for rice (clay / loamy soil)",
                "Typical dose per acre: 50 kg N, 24 kg P2O5 and 24 kg K2O. Apply all P and K as basal, and split N into basal, tillering and panicle initiation. Add zinc sulphate 10 kg/acre in zinc-deficient soils. Get a soil test for exact doses."),
            new Advisory("FERTILIZER", "Wheat", "Fertilizer for wheat (loamy / alluvial soil)",
                "Typical dose per acre: 48 kg N, 24 kg P2O5 and 16 kg K2O. Apply half of N with full P and K at sowing, and the rest at first irrigation."),
            new Advisory("FERTILIZER", "Cotton", "Fertilizer for cotton (black soil)",
                "Typical dose per acre: 48 kg N, 24 kg P2O5 and 24 kg K2O. Split N in 3 doses: sowing, squaring and flowering. Add 4 tonnes of farmyard manure per acre before sowing."),
            new Advisory("FERTILIZER", "Tomato", "Fertilizer for tomato (red / sandy loam soil)",
                "Apply 8 tonnes of FYM per acre, plus about 50 kg N, 30 kg P2O5 and 30 kg K2O. Split N and K across transplanting, flowering and fruit set. Add calcium to avoid blossom end rot."),
            new Advisory("FERTILIZER", "Maize", "Fertilizer for maize",
                "Typical dose per acre: 48 kg N, 24 kg P2O5 and 16 kg K2O. Apply N in three splits: sowing, knee-high and pre-tasseling."),
            // ---- Crop disease information (symptoms & treatment) ----
            new Advisory("DISEASE", "Rice", "Blast",
                "Symptoms: spindle-shaped grey-centred spots with brown margins on leaves, and neck rot at panicle. Treatment: use resistant varieties, avoid excess nitrogen, and spray tricyclazole or isoprothiolane as per label directions."),
            new Advisory("DISEASE", "Rice", "Bacterial leaf blight",
                "Symptoms: yellow to white stripes from leaf tips drying downward. Treatment: use certified seed, avoid excess nitrogen, drain the field, and apply copper-based bactericide as advised locally."),
            new Advisory("DISEASE", "Wheat", "Rust (yellow / brown)",
                "Symptoms: yellow or orange-brown powdery pustules on leaves. Treatment: grow resistant varieties, sow on time, and spray propiconazole as per label directions at the first sign."),
            new Advisory("DISEASE", "Tomato", "Early blight",
                "Symptoms: dark brown spots with concentric rings on older leaves. Treatment: remove infected leaves, rotate crops, keep leaves dry, and spray mancozeb or chlorothalonil per label directions."),
            new Advisory("DISEASE", "Tomato", "Leaf curl virus",
                "Symptoms: upward curling, yellowing and stunting, spread by whitefly. Treatment: control whitefly with yellow sticky traps and recommended insecticides, remove infected plants, and use resistant hybrids."),
            new Advisory("DISEASE", "Cotton", "Pink bollworm",
                "Symptoms: rosette flowers and damaged bolls with larvae inside. Treatment: use pheromone traps, destroy crop residue after harvest, and follow local agriculture-department spray advisories."),
            new Advisory("DISEASE", "Maize", "Fall armyworm",
                "Symptoms: ragged leaf feeding and frass in the whorl. Treatment: scout early, apply neem-based spray or recommended insecticide into the whorl, and use pheromone traps."),
            // ---- Best practices & seasonal tips ----
            new Advisory("PRACTICE", "General", "Kharif season (June-October)",
                "Sow rice, maize, cotton, soybean and groundnut with the monsoon onset. Prepare bunds and drainage, and use certified seed."),
            new Advisory("PRACTICE", "General", "Rabi season (October-March)",
                "Sow wheat, mustard, chickpea and peas after the monsoon. Plan irrigation schedules and protect against frost."),
            new Advisory("PRACTICE", "General", "Soil health",
                "Test soil every 2-3 years, rotate crops with legumes, add organic matter, and avoid over-use of chemical fertilizers.")
        ));
    }
}

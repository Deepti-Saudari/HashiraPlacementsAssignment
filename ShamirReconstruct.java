import java.io.FileReader;
import java.math.BigInteger;
import java.util.Map;
import com.google.gson.*;

public class ShamirReconstruct {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Please provide JSON file as argument!");
            return;
        }
        Gson gson = new Gson();
        JsonObject root = gson.fromJson(new FileReader(args[0]), JsonObject.class);

      
        int n = root.getAsJsonObject("keys").get("n").getAsInt();
        int k = root.getAsJsonObject("keys").get("k").getAsInt();

        BigInteger[] x = new BigInteger[k];
        BigInteger[] y = new BigInteger[k];

        int index = 0;
        for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
            if (entry.getKey().equals("keys")) continue;
            if (index >= k) break;

            JsonObject obj = entry.getValue().getAsJsonObject();
            int base = Integer.parseInt(obj.get("base").getAsString());
            BigInteger value = new BigInteger(obj.get("value").getAsString(), base);

            x[index] = BigInteger.valueOf(Long.parseLong(entry.getKey())); 
            y[index] = value;
            index++;
        }

        BigInteger P = new BigInteger("100000000000000000000000000000000000000003");

        BigInteger secret = BigInteger.ZERO;
        for (int j = 0; j < k; j++) {
            BigInteger num = BigInteger.ONE;
            BigInteger den = BigInteger.ONE;

            for (int m = 0; m < k; m++) {
                if (m != j) {
                    num = num.multiply(x[m].negate()).mod(P); 
                    den = den.multiply(x[j].subtract(x[m])).mod(P);
                }
            }

            BigInteger term = y[j].multiply(num).mod(P)
                              .multiply(den.modInverse(P)).mod(P);
            secret = secret.add(term).mod(P);
        }

        System.out.println("Secret (constant term P(0)) = " + secret);
    }
}

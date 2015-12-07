import java.lang.Math;
import java.lang.String;
import java.net.URLEncoder;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.protocol.http.util.HTTPArgument;

HTTPSamplerBase httpSamplerBase = (HTTPSamplerBase) sampler;

for(int i = 1; i <= Integer.valueOf(vars.get("KPRIM_REF_matchNr")); i+=2){
    final String answer = vars.get("KPRIM_REF_" + (i + ((Math.random() < 0.5) ? 0: 1)));

    String name = "name=\"";
    int nameStart = answer.lastIndexOf(name) + name.length();
    int nameEnd = answer.indexOf("\"", nameStart);
    
    String value = "value=\"";
    int valueStart = answer.lastIndexOf(value) + value.length();
    int valueEnd = answer.indexOf("\"", valueStart);
    
    httpSamplerBase.addEncodedArgument(URLEncoder.encode(answer.substring(nameStart, nameEnd), "UTF-8"), URLEncoder.encode(answer.substring(valueStart, valueEnd), "UTF-8"));
}

httpSamplerBase.addEncodedArgument(URLEncoder.encode("olat_fosm", "UTF-8"), "Save+answer+");

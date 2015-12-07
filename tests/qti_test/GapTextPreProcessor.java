import java.lang.Math;
import java.lang.String;
import java.net.URLEncoder;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.protocol.http.util.HTTPArgument;

HTTPSamplerBase httpSamplerBase = (HTTPSamplerBase) sampler;

for(int i = 1; i <= Integer.valueOf(vars.get("GAP_TEXT_REF_matchNr")); i++){
    final String answer = vars.get("GAP_TEXT_REF_" + i);

    String name = "name=\"";
    int nameStart = answer.lastIndexOf(name) + name.length();
    int nameEnd = answer.indexOf("\"", nameStart);
    
    httpSamplerBase.addEncodedArgument(URLEncoder.encode(answer.substring(nameStart, nameEnd), "UTF-8"), URLEncoder.encode(Double.toString(Math.floor(Math.random()*100000)), "UTF-8"));
}

httpSamplerBase.addEncodedArgument(URLEncoder.encode("olat_fosm", "UTF-8"), "Save+answer+");

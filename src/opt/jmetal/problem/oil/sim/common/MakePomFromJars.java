package opt.jmetal.problem.oil.sim.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.dom4j.Element;
import org.dom4j.dom.DOMElement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.alibaba.fastjson.JSONObject;

public class MakePomFromJars {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        Element dependencys = new DOMElement("dependencies");
        File dir = new File("lib");
        for (File jar : dir.listFiles()) {
            JarInputStream jis = new JarInputStream(new FileInputStream(jar));
            Manifest mainmanifest = jis.getManifest();
            jis.close();
            if (mainmanifest == null) {
                System.err.println(jar.getName());
                continue;
            }
            String bundleName = mainmanifest.getMainAttributes().getValue("Bundle-Name");
            String bundleVersion = mainmanifest.getMainAttributes().getValue("Bundle-Version");
            Element ele = null;

            StringBuffer sb = new StringBuffer(jar.getName());
            if (bundleName != null) {
                bundleName = bundleName.toLowerCase().replace(" ", "-");
                sb.append(bundleName + "\t").append(bundleVersion);
                ele = getDependices(bundleName, bundleVersion);
                // System.out.println(sb.toString());
                // System.out.println(ele.asXML());
            }
            if (ele == null || ele.elements().size() == 0) {
                bundleName = "";
                bundleVersion = "";
                String[] ns = jar.getName().replace(".jar", "").split("-");
                for (String s : ns) {
                    if (Character.isDigit(s.charAt(0))) {
                        bundleVersion += s + "-";
                    } else {
                        bundleName += s + "-";
                    }
                }
                if (bundleVersion.endsWith("-")) {
                    bundleVersion = bundleVersion.substring(0, bundleVersion.length() - 1);
                }
                if (bundleName.endsWith("-")) {
                    bundleName = bundleName.substring(0, bundleName.length() - 1);
                }
                ele = getDependices(bundleName, bundleVersion);
                sb.setLength(0);
                sb.append(bundleName + "\t").append(bundleVersion);
                // System.out.println(sb.toString());
                // System.out.println(ele.asXML());
            }
            ele = getDependices(bundleName, bundleVersion);
            if (ele.elements().size() == 0) {
                ele.add(new DOMElement("groupId").addText("not find"));
                ele.add(new DOMElement("artifactId").addText(bundleName));
                ele.add(new DOMElement("version").addText(bundleVersion));
                System.out.println(
                        sb.toString() + " not find, please visit https://mvnrepository.com/ to get more info.");
            }
            dependencys.add(ele);
            // System.out.println();
        }
        System.out.println(dependencys.asXML());
    }

    public static Element getDependices(String key, String ver) {
        Element dependency = new DOMElement("dependency");
        // 设置代理
        // System.setProperty("http.proxyHost", "127.0.0.1");
        // System.setProperty("http.proxyPort", "8090");
        try {
            String url = "http://search.maven.org/solrsearch/select?q=a%3A%22" + key + "%22%20AND%20v%3A%22" + ver
                    + "%22&rows=3&wt=json";
            Document doc = Jsoup.connect(url).ignoreContentType(true).timeout(30000).get();
            String elem = doc.body().text();
            JSONObject response = JSONObject.parseObject(elem).getJSONObject("response");
            if (response.containsKey("docs") && response.getJSONArray("docs").size() > 0) {
                JSONObject docJson = response.getJSONArray("docs").getJSONObject(0);
                Element groupId = new DOMElement("groupId");
                Element artifactId = new DOMElement("artifactId");
                Element version = new DOMElement("version");
                groupId.addText(docJson.getString("g"));
                artifactId.addText(docJson.getString("a"));
                version.addText(docJson.getString("v"));
                dependency.add(groupId);
                dependency.add(artifactId);
                dependency.add(version);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dependency;
    }
}
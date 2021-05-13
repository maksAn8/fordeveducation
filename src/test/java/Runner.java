import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        // путь в features-файлам
        features = {"src/test/resources/Tests/"},
        // путь к классам StepsDefinition
        glue = {"Steps"},
        // Формат отчета, помещается в target-ветке
        plugin = {
                "pretty", "json:target/runner.json", "junit:target/junit.xml",
        }
        //,tags = {"@C1"}
        )
public class Runner {
}


package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration

import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity


@DataJpaTest
class ValidateActivityTest extends SpockTest {
    def activityDto
    def themeDto
    def activity
    def theme

    def setup() {
        themeDto = new ThemeDto()
        themeDto.setName("THEME_1_NAME")

        theme = themeService.registerTheme(themeDto);

        List<Theme> themes = new ArrayList<>()
        themes.add(theme)
        activityDto = new ActivityDto()
        activityDto.setName("ACTIVITY_1_NAME")
        activityDto.setRegion("ACTIVITY_1_REGION")
        activityDto.setThemes(themes)

        activity = activityService.registerActivity(activityDto)
    }

    def "validate activity with success"() {
        when:
        //themeService.validateTheme(theme.getId())
        activityService.validateActivity(activity.getId())

        then: "the activity and theme are validated"
        activity.getState() == Activity.State.APPROVED
   }

    def "the activity doesn't exist"() {
        when:
        activityService.validateActivity(activity.getId() + 1)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_NOT_FOUND
    }

    def "the activity is already approved"() {
        when:
        activityService.validateActivity(activity.getId())
        activityService.validateActivity(activity.getId())

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_ALREADY_APPROVED
    }

    /*def "the theme haven't yet been approved"() {
        when:
        activityService.validateActivity(activity.getId())
        theme.setState(Activity.State.SUBMITTED)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.THEME_NOT_APPROVED
    }*/

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
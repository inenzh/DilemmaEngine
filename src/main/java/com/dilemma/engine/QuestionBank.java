package com.dilemma.engine;

import com.dilemma.model.Question;
import com.dilemma.model.Question.Option;
import com.dilemma.model.Question.Tier;

import java.util.*;

import static com.dilemma.model.Attributes.*;

/**
 * The fixed set of 30 dilemmas. Content and hidden scoring weights live only
 * here and are never transmitted to the client verbatim.
 */
public final class QuestionBank {

    private static final List<Question> BANK = build();

    public static List<Question> all() {
        return BANK;
    }

    public static Question byId(int id) {
        for (Question q : BANK) if (q.id == id) return q;
        throw new NoSuchElementException("No question with id " + id);
    }

    // ---- construction helpers -------------------------------------------------

    private static Map<String, Integer> d(Object... kv) {
        Map<String, Integer> m = new HashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            m.put((String) kv[i], (Integer) kv[i + 1]);
        }
        return m;
    }

    private static Option opt(String text, Map<String, Integer> deltas) {
        return new Option(text, deltas, null);
    }

    private static Option opt(String text, Map<String, Integer> deltas, String axis) {
        return new Option(text, deltas, axis);
    }

    private static Question q(int id, Tier tier, String text, List<String> touches, Integer pairsWith,
                               Option a, Option b, Option c, Option e) {
        return new Question(id, tier, text, Arrays.asList(a, b, c, e), touches, pairsWith);
    }

    private static List<String> attrs(String... a) {
        return Arrays.asList(a);
    }

    // ---- the 30 dilemmas --------------------------------------------------

    private static List<Question> build() {
        List<Question> list = new ArrayList<>();

        list.add(q(1, Tier.BROAD,
            "A close friend on your team let a junior colleague take the blame for a mistake that was " +
            "actually your friend's fault, to protect their own record before a big promotion round. " +
            "Your manager asks you privately if you know what really happened.",
            attrs(MORAL_RIGIDITY, EMPATHY), 19,
            opt("Tell your manager it was an honest team error, without naming names.", d(EMPATHY,6, MORAL_RIGIDITY,-4, STRATEGIC_THINKING,1), "A"),
            opt("Tell your manager exactly what your friend did.", d(MORAL_RIGIDITY,7, STRATEGIC_THINKING,1, EMPATHY,-2), "B"),
            opt("Tell your friend to come clean themselves within the week, or you will.", d(STRATEGIC_THINKING,5, MORAL_RIGIDITY,3, RISK_TOLERANCE,-2)),
            opt("Say nothing unless you're directly asked about your friend by name.", d(RISK_TOLERANCE,-4, STRATEGIC_THINKING,-2, EMPATHY,2))
        ));

        list.add(q(2, Tier.BROAD,
            "During a fast-spreading outbreak, your city proposes a mandatory phone-tracking app that would " +
            "cut infections significantly but permanently normalizes government location tracking. You sit " +
            "on the advisory board casting a recommendation.",
            attrs(RISK_TOLERANCE, POWER_ORIENTATION), null,
            opt("Recommend mandatory adoption; lives now outweigh a hypothetical future risk.", d(STRATEGIC_THINKING,6, RISK_TOLERANCE,3, POWER_ORIENTATION,4, MORAL_RIGIDITY,-2)),
            opt("Recommend a voluntary version with strong incentives instead.", d(EMPATHY,4, STRATEGIC_THINKING,3, RISK_TOLERANCE,-2)),
            opt("Oppose it outright; the precedent is too dangerous no matter the death toll.", d(MORAL_RIGIDITY,8, RISK_TOLERANCE,-1, STRATEGIC_THINKING,-3)),
            opt("Recommend mandatory adoption but with a hard sunset clause you will personally fight to enforce.", d(STRATEGIC_THINKING,7, POWER_ORIENTATION,3, MORAL_RIGIDITY,2, RISK_TOLERANCE,1))
        ));

        list.add(q(3, Tier.BROAD,
            "You learn your best friend's spouse is having an affair. Your friend has no idea and is planning " +
            "a big anniversary celebration next week.",
            attrs(EMPATHY, MORAL_RIGIDITY), null,
            opt("Tell your friend before the celebration, however it lands.", d(MORAL_RIGIDITY,6, EMPATHY,1, STRATEGIC_THINKING,-1)),
            opt("Say nothing; it isn't your place to break this.", d(EMPATHY,3, MORAL_RIGIDITY,-3, RISK_TOLERANCE,-2)),
            opt("Confront the spouse privately and demand they tell your friend themselves.", d(POWER_ORIENTATION,5, STRATEGIC_THINKING,3, RISK_TOLERANCE,2)),
            opt("Wait until after the celebration, then tell your friend.", d(STRATEGIC_THINKING,4, EMPATHY,4, MORAL_RIGIDITY,-1))
        ));

        list.add(q(4, Tier.BROAD,
            "Your team is behind on a deadline. You could ship a feature with a shortcut that works today but " +
            "will likely need a costly rebuild in a year, or push the deadline back two weeks to do it properly.",
            attrs(STRATEGIC_THINKING, RISK_TOLERANCE), null,
            opt("Ship the shortcut; you can deal with the rebuild later.", d(RISK_TOLERANCE,5, STRATEGIC_THINKING,-4, POWER_ORIENTATION,1)),
            opt("Push the deadline back and do it properly.", d(STRATEGIC_THINKING,7, MORAL_RIGIDITY,2, RISK_TOLERANCE,-2)),
            opt("Ship the shortcut but immediately schedule the rebuild on the roadmap.", d(STRATEGIC_THINKING,5, RISK_TOLERANCE,2, POWER_ORIENTATION,2)),
            opt("Escalate to leadership and let them choose.", d(POWER_ORIENTATION,-4, STRATEGIC_THINKING,1, RISK_TOLERANCE,-3))
        ));

        list.add(q(5, Tier.BROAD,
            "You're one of two finalists for a promotion. You could frame true but unflattering facts about the " +
            "other finalist's project to leadership, or spend that same week helping a struggling junior " +
            "colleague finish their own project, with no benefit to your case.",
            attrs(EMPATHY, POWER_ORIENTATION), null,
            opt("Use the framing; the promotion matters to your life.", d(POWER_ORIENTATION,6, MORAL_RIGIDITY,-5, STRATEGIC_THINKING,2)),
            opt("Spend the week helping the junior colleague instead.", d(EMPATHY,8, MORAL_RIGIDITY,2, POWER_ORIENTATION,-4)),
            opt("Focus purely on making your own project better.", d(STRATEGIC_THINKING,5, POWER_ORIENTATION,1, MORAL_RIGIDITY,1)),
            opt("Mention the finalist's weak point once, factually, only if leadership asks.", d(STRATEGIC_THINKING,3, MORAL_RIGIDITY,-1, POWER_ORIENTATION,2))
        ));

        list.add(q(6, Tier.BROAD,
            "You can stay in a stable job with modest but guaranteed growth, or take an offer at an early-stage " +
            "venture with real mission alignment, double the eventual upside, and a real chance it fails within a year.",
            attrs(RISK_TOLERANCE), null,
            opt("Take the stable job.", d(RISK_TOLERANCE,-6, STRATEGIC_THINKING,1)),
            opt("Take the venture; the upside and mission are worth the risk.", d(RISK_TOLERANCE,8, POWER_ORIENTATION,2, STRATEGIC_THINKING,-1)),
            opt("Take the venture but negotiate a shorter trial period first.", d(RISK_TOLERANCE,4, STRATEGIC_THINKING,5)),
            opt("Stay put for now and revisit it in a year.", d(RISK_TOLERANCE,-4, STRATEGIC_THINKING,3))
        ));

        list.add(q(7, Tier.BROAD,
            "A regular customer with a documented hardship needs an exception to a strict no-refund policy. " +
            "Approving it will help them significantly; the policy exists to prevent abuse, and you'll need to " +
            "justify any exception if audited.",
            attrs(MORAL_RIGIDITY, STRATEGIC_THINKING), null,
            opt("Deny the refund; policy exists for a reason and exceptions invite abuse.", d(MORAL_RIGIDITY,8, EMPATHY,2, STRATEGIC_THINKING,-1)),
            opt("Approve it and document a clear justification for future audits.", d(STRATEGIC_THINKING,7, EMPATHY,4, MORAL_RIGIDITY,2)),
            opt("Approve it quietly and hope it never comes up.", d(RISK_TOLERANCE,8, MORAL_RIGIDITY,-4)),
            opt("Escalate to a supervisor rather than deciding yourself.", d(STRATEGIC_THINKING,5, RISK_TOLERANCE,-2, EMPATHY,2))
        ));

        list.add(q(8, Tier.BROAD,
            "You manage a small team and have one discretionary bonus to award. One person delivered the best " +
            "individual results this quarter; another carried the team through a crisis no one will ever see " +
            "credit for, at real cost to their own numbers.",
            attrs(EMPATHY, STRATEGIC_THINKING), null,
            opt("Give it to the top individual performer; results should be rewarded.", d(STRATEGIC_THINKING,4, POWER_ORIENTATION,3, EMPATHY,-2)),
            opt("Give it to the person who carried the team, even though it isn't in the numbers.", d(EMPATHY,7, MORAL_RIGIDITY,2, STRATEGIC_THINKING,-1)),
            opt("Split it evenly between them.", d(EMPATHY,3, STRATEGIC_THINKING,-2, MORAL_RIGIDITY,1)),
            opt("Base it on the formula you set at the start of the quarter, regardless of how you feel now.", d(MORAL_RIGIDITY,7, STRATEGIC_THINKING,2, POWER_ORIENTATION,-1))
        ));

        list.add(q(9, Tier.MID,
            "An employee you like has made the same costly mistake twice after being clearly warned. A third " +
            "mistake would become their manager's problem to explain upward, not just theirs.",
            attrs(MORAL_RIGIDITY, EMPATHY), null,
            opt("Give them one more clear, final chance.", d(EMPATHY,6, MORAL_RIGIDITY,-2, RISK_TOLERANCE,2)),
            opt("Let them go now; the pattern is the risk, not the person.", d(MORAL_RIGIDITY,6, POWER_ORIENTATION,3, EMPATHY,-3)),
            opt("Reassign them to a role with less exposure instead of firing them.", d(STRATEGIC_THINKING,6, EMPATHY,4, MORAL_RIGIDITY,-1)),
            opt("Document it formally and let the standard HR process decide.", d(STRATEGIC_THINKING,3, POWER_ORIENTATION,-4, MORAL_RIGIDITY,2))
        ));

        list.add(q(10, Tier.MID,
            "You lead a project where the team is split on a major direction. As lead, you personally believe " +
            "you know the right call, but a full team vote would probably go the other way.",
            attrs(POWER_ORIENTATION), 24,
            opt("Make the call yourself; that's what leadership is for.", d(POWER_ORIENTATION,7, STRATEGIC_THINKING,2, MORAL_RIGIDITY,1), "A"),
            opt("Put it to a vote and commit to the result.", d(POWER_ORIENTATION,-4, EMPATHY,3, MORAL_RIGIDITY,2), "B"),
            opt("Make the call yourself, but explain your full reasoning afterward.", d(POWER_ORIENTATION,5, STRATEGIC_THINKING,4), "A"),
            opt("Delay the decision until you can build enough consensus for your view.", d(STRATEGIC_THINKING,5, POWER_ORIENTATION,1, RISK_TOLERANCE,-2))
        ));

        list.add(q(11, Tier.MID,
            "You discover your mentor — the person who got you your current role — has been quietly inflating " +
            "expense reports for years. It's not a large amount, but it's textbook fraud.",
            attrs(MORAL_RIGIDITY, EMPATHY), null,
            opt("Report it through the proper channel.", d(MORAL_RIGIDITY,7, STRATEGIC_THINKING,1, EMPATHY,-2, POWER_ORIENTATION,1)),
            opt("Talk to your mentor privately and ask them to stop and self-correct.", d(EMPATHY,5, STRATEGIC_THINKING,3, MORAL_RIGIDITY,1)),
            opt("Say nothing; it's a small amount and not your job to police it.", d(EMPATHY,3, MORAL_RIGIDITY,-5, RISK_TOLERANCE,-1)),
            opt("Report it, but only after quietly warning your mentor first.", d(MORAL_RIGIDITY,4, EMPATHY,3, STRATEGIC_THINKING,2))
        ));

        list.add(q(12, Tier.MID,
            "Packages have been disappearing from your shared building. You have a way to check your roommate's " +
            "location history to rule them out as a suspect, without asking permission.",
            attrs(MORAL_RIGIDITY, RISK_TOLERANCE), null,
            opt("Check it; ruling people out matters more than asking permission.", d(RISK_TOLERANCE,5, MORAL_RIGIDITY,-3, POWER_ORIENTATION,2)),
            opt("Don't check it; trust and consent matter more than solving this quickly.", d(MORAL_RIGIDITY,6, EMPATHY,2, RISK_TOLERANCE,-3)),
            opt("Ask your roommate directly for their alibi instead.", d(STRATEGIC_THINKING,6, EMPATHY,3)),
            opt("Report the pattern to building security and let them investigate everyone.", d(POWER_ORIENTATION,-3, STRATEGIC_THINKING,4, MORAL_RIGIDITY,2))
        ));

        list.add(q(13, Tier.MID,
            "A coworker deliberately took credit for your idea in a meeting that mattered, in front of the people " +
            "who decide promotions. You now have a clean chance to do the same to them next week.",
            attrs(MORAL_RIGIDITY, POWER_ORIENTATION), 27,
            opt("Do it back; they set the terms.", d(POWER_ORIENTATION,4, MORAL_RIGIDITY,-5, RISK_TOLERANCE,2), "A"),
            opt("Let it go and refocus on your own work.", d(MORAL_RIGIDITY,5, EMPATHY,1, STRATEGIC_THINKING,2), "B"),
            opt("Raise it directly and privately with the coworker.", d(STRATEGIC_THINKING,6, MORAL_RIGIDITY,2)),
            opt("Document it and mention it neutrally if the topic ever comes up.", d(STRATEGIC_THINKING,5, RISK_TOLERANCE,-1))
        ));

        list.add(q(14, Tier.MID,
            "A new zoning law would block you from expanding your family's small business, but it protects a " +
            "neighborhood park most of the community relies on.",
            attrs(EMPATHY, MORAL_RIGIDITY), 25,
            opt("Fight the law; you built your business, it's your right.", d(POWER_ORIENTATION,3, MORAL_RIGIDITY,3, STRATEGIC_THINKING,-1), "A"),
            opt("Accept the law; the community's shared good outweighs your plan.", d(EMPATHY,5, MORAL_RIGIDITY,2, STRATEGIC_THINKING,1), "B"),
            opt("Propose a compromise that shrinks your expansion instead of scrapping it.", d(STRATEGIC_THINKING,7, EMPATHY,2)),
            opt("Relocate the business rather than fight the law.", d(RISK_TOLERANCE,3, STRATEGIC_THINKING,3))
        ));

        list.add(q(15, Tier.MID,
            "New leadership wants to overhaul a system your team built years ago that works fine but is " +
            "old-fashioned. The change is risky but could be a real improvement; the current system just quietly works.",
            attrs(RISK_TOLERANCE, STRATEGIC_THINKING), null,
            opt("Support the overhaul fully.", d(RISK_TOLERANCE,5, STRATEGIC_THINKING,3, POWER_ORIENTATION,1)),
            opt("Push back and defend what already works.", d(MORAL_RIGIDITY,4, RISK_TOLERANCE,-4, STRATEGIC_THINKING,-1)),
            opt("Support a phased pilot before committing fully.", d(STRATEGIC_THINKING,7, RISK_TOLERANCE,1)),
            opt("Support it publicly but slow-walk it in practice.", d(POWER_ORIENTATION,2, STRATEGIC_THINKING,2, MORAL_RIGIDITY,-4))
        ));

        list.add(q(16, Tier.MID,
            "Budget cuts mean laying off 10% of a department. You could cut based purely on performance metrics, " +
            "or weigh personal circumstances — a single parent, someone with a sick family member — even though " +
            "the numbers don't ask you to.",
            attrs(EMPATHY, MORAL_RIGIDITY), 29,
            opt("Cut purely by the metrics; anything else is unfair to everyone else.", d(STRATEGIC_THINKING,5, MORAL_RIGIDITY,4, EMPATHY,-3), "A"),
            opt("Weigh personal circumstances heavily alongside the numbers.", d(EMPATHY,8, MORAL_RIGIDITY,-2, STRATEGIC_THINKING,-1), "B"),
            opt("Use the metrics, but let affected people choose severance vs. reduced hours.", d(STRATEGIC_THINKING,6, EMPATHY,3)),
            opt("Push back on leadership to reduce the number of layoffs instead.", d(POWER_ORIENTATION,-2, EMPATHY,4, RISK_TOLERANCE,2))
        ));

        list.add(q(17, Tier.MID,
            "Your platform hosts content that's legal but frequently offensive and driving some users away. You " +
            "could moderate it more heavily, at the cost of also restricting some legitimate unpopular speech.",
            attrs(MORAL_RIGIDITY, POWER_ORIENTATION), 23,
            opt("Moderate heavily; a healthier space is worth some restriction.", d(POWER_ORIENTATION,4, MORAL_RIGIDITY,2, EMPATHY,2), "B"),
            opt("Leave it largely open; restricting legal speech is the worse harm.", d(MORAL_RIGIDITY,5, RISK_TOLERANCE,2, STRATEGIC_THINKING,-1), "A"),
            opt("Let users filter it themselves instead of moderating centrally.", d(STRATEGIC_THINKING,6, POWER_ORIENTATION,-2)),
            opt("Moderate only the content advertisers explicitly object to.", d(POWER_ORIENTATION,3, STRATEGIC_THINKING,4, MORAL_RIGIDITY,-3))
        ));

        list.add(q(18, Tier.MID,
            "A well-funded company offers you a significant raise and equity to lead a team whose product you " +
            "find ethically uncomfortable, though it's fully legal.",
            attrs(MORAL_RIGIDITY, POWER_ORIENTATION), 28,
            opt("Take it; you can influence it for the better from the inside.", d(POWER_ORIENTATION,4, STRATEGIC_THINKING,4, RISK_TOLERANCE,2)),
            opt("Decline; you won't lend your name and skill to it.", d(MORAL_RIGIDITY,7, RISK_TOLERANCE,-2), "B"),
            opt("Take it for the money, planning to move on in a year or two.", d(POWER_ORIENTATION,3, RISK_TOLERANCE,3, MORAL_RIGIDITY,-5), "A"),
            opt("Ask for a role on a different, less objectionable team at the same company.", d(STRATEGIC_THINKING,6, RISK_TOLERANCE,-1))
        ));

        list.add(q(19, Tier.MID,
            "Years later, a colleague you once quietly protected — the same kind of pattern as before — is up " +
            "for a major industry award. You're on the nominating panel and know they once took credit for " +
            "someone else's work.",
            attrs(MORAL_RIGIDITY, EMPATHY), 1,
            opt("Say nothing; it was a long time ago and not your fight.", d(EMPATHY,3, MORAL_RIGIDITY,-4, RISK_TOLERANCE,-2), "A"),
            opt("Raise it with the panel before the vote.", d(MORAL_RIGIDITY,7, STRATEGIC_THINKING,1, POWER_ORIENTATION,1), "B"),
            opt("Vote against them privately without explaining why.", d(RISK_TOLERANCE,-3, MORAL_RIGIDITY,2, STRATEGIC_THINKING,2)),
            opt("Reach out to them privately first and ask them to withdraw.", d(EMPATHY,4, STRATEGIC_THINKING,5, MORAL_RIGIDITY,1))
        ));

        list.add(q(20, Tier.MID,
            "You have one remaining seat in an oversubscribed training program with real career impact. One " +
            "candidate is more likely to succeed and multiply the benefit onto their whole team; the other needs " +
            "it most and has no other path forward.",
            attrs(EMPATHY, STRATEGIC_THINKING), null,
            opt("Give it to the one most likely to succeed and spread the benefit.", d(STRATEGIC_THINKING,7, POWER_ORIENTATION,1, EMPATHY,-2)),
            opt("Give it to the one who needs it most.", d(EMPATHY,7, MORAL_RIGIDITY,1, STRATEGIC_THINKING,-2)),
            opt("Ask your own manager for a second seat instead of choosing.", d(STRATEGIC_THINKING,3, POWER_ORIENTATION,-3, RISK_TOLERANCE,-1)),
            opt("Run a fair random draw between the two.", d(MORAL_RIGIDITY,4, STRATEGIC_THINKING,-1, RISK_TOLERANCE,2))
        ));

        list.add(q(21, Tier.HARD,
            "A fast-moving outbreak is confirmed in one small town. Sealing it immediately would almost certainly " +
            "stop national spread, but would trap and likely doom some residents who aren't yet infected. Waiting " +
            "for full test results protects due process but risks the outbreak going national.",
            attrs(RISK_TOLERANCE, STRATEGIC_THINKING, POWER_ORIENTATION), null,
            opt("Seal the town immediately.", d(STRATEGIC_THINKING,8, POWER_ORIENTATION,4, EMPATHY,-4, RISK_TOLERANCE,2)),
            opt("Wait for full test results before deciding, whatever the national risk.", d(MORAL_RIGIDITY,6, RISK_TOLERANCE,-3, EMPATHY,2)),
            opt("Seal it, but evacuate confirmed-negative residents first under strict controls.", d(STRATEGIC_THINKING,9, EMPATHY,3, RISK_TOLERANCE,2)),
            opt("Refuse to make the call yourself and push it to elected officials.", d(POWER_ORIENTATION,-6, STRATEGIC_THINKING,1, RISK_TOLERANCE,-2))
        ));

        list.add(q(22, Tier.HARD,
            "As a senior official, you could quietly redirect funds from a legal but wasteful program into an " +
            "unauthorized effort you're certain will save more lives — breaking procurement law to do it.",
            attrs(MORAL_RIGIDITY, POWER_ORIENTATION), null,
            opt("Redirect the funds; the outcome justifies breaking this rule.", d(POWER_ORIENTATION,7, STRATEGIC_THINKING,3, MORAL_RIGIDITY,-6, RISK_TOLERANCE,3)),
            opt("Follow the law and lobby to change the program properly instead.", d(MORAL_RIGIDITY,8, STRATEGIC_THINKING,-1, POWER_ORIENTATION,-2)),
            opt("Redirect the funds, but disclose it immediately and accept the consequences.", d(POWER_ORIENTATION,5, MORAL_RIGIDITY,-2, STRATEGIC_THINKING,2, RISK_TOLERANCE,1)),
            opt("Do neither; resign so someone else has to make the call.", d(RISK_TOLERANCE,-5, POWER_ORIENTATION,-6, MORAL_RIGIDITY,3))
        ));

        list.add(q(23, Tier.HARD,
            "Intelligence suggests blanket surveillance of a specific community's communications would likely " +
            "prevent a serious attack, but would mean monitoring thousands of innocent people without their " +
            "knowledge or consent, permanently.",
            attrs(MORAL_RIGIDITY, POWER_ORIENTATION), 17,
            opt("Authorize it; preventing the attack outweighs the privacy cost.", d(POWER_ORIENTATION,6, STRATEGIC_THINKING,2, MORAL_RIGIDITY,-2, EMPATHY,-2), "B"),
            opt("Refuse it; permanent mass surveillance of innocents isn't worth any single prevention.", d(MORAL_RIGIDITY,8, RISK_TOLERANCE,-2), "A"),
            opt("Authorize a narrower, time-limited version with independent oversight.", d(STRATEGIC_THINKING,8, POWER_ORIENTATION,2, MORAL_RIGIDITY,1)),
            opt("Authorize it, but only after independent court approval.", d(STRATEGIC_THINKING,5, POWER_ORIENTATION,-2, MORAL_RIGIDITY,3))
        ));

        list.add(q(24, Tier.HARD,
            "A democratically elected leader is dismantling checks on their own power through legal but corrupt " +
            "means. A group of officials you trust asks you to help remove them by force before the next " +
            "election, which they will likely rig.",
            attrs(POWER_ORIENTATION, RISK_TOLERANCE), 10,
            opt("Support removing them by force.", d(POWER_ORIENTATION,6, RISK_TOLERANCE,5, MORAL_RIGIDITY,-3, STRATEGIC_THINKING,1), "A"),
            opt("Refuse; even a corrupt elected leader must be removed through legal and electoral means.", d(MORAL_RIGIDITY,7, STRATEGIC_THINKING,2, RISK_TOLERANCE,-3), "B"),
            opt("Refuse the coup, but actively organize legal resistance and international pressure instead.", d(STRATEGIC_THINKING,8, MORAL_RIGIDITY,3, POWER_ORIENTATION,1)),
            opt("Stay out of it entirely.", d(RISK_TOLERANCE,-4, POWER_ORIENTATION,-5, STRATEGIC_THINKING,-2))
        ));

        list.add(q(25, Tier.HARD,
            "You sit on a hospital ethics board setting the rule for a new organ allocation policy. A pure " +
            "lottery treats everyone as equal; a utility-weighted system saves more total years of life but " +
            "systematically deprioritizes older and sicker patients.",
            attrs(EMPATHY, STRATEGIC_THINKING, MORAL_RIGIDITY), 14,
            opt("Adopt the utility-weighted system; it saves more lives overall.", d(STRATEGIC_THINKING,8, POWER_ORIENTATION,2, EMPATHY,-3), "B"),
            opt("Adopt a pure lottery; every life counts equally regardless of prognosis.", d(MORAL_RIGIDITY,7, EMPATHY,3, STRATEGIC_THINKING,-2), "A"),
            opt("Adopt a hybrid: lottery among comparable prognoses, weighting only in clear-cut cases.", d(STRATEGIC_THINKING,9, EMPATHY,2, MORAL_RIGIDITY,1)),
            opt("Delegate the final formula to an outside panel of ethicists.", d(POWER_ORIENTATION,-4, STRATEGIC_THINKING,2, RISK_TOLERANCE,-1))
        ));

        list.add(q(26, Tier.HARD,
            "You're subpoenaed in a case where your honest testimony would be the deciding evidence that sends a " +
            "family member to prison for a serious crime you know they committed.",
            attrs(MORAL_RIGIDITY, EMPATHY), null,
            opt("Testify fully and honestly.", d(MORAL_RIGIDITY,8, STRATEGIC_THINKING,1, EMPATHY,-3)),
            opt("Testify, but answer as narrowly and unhelpfully as legally allowed.", d(EMPATHY,4, MORAL_RIGIDITY,-2, STRATEGIC_THINKING,3, RISK_TOLERANCE,1)),
            opt("Refuse to testify and accept the legal consequences for yourself.", d(EMPATHY,6, MORAL_RIGIDITY,-1, RISK_TOLERANCE,4)),
            opt("Testify honestly, then do everything you can to support them through the sentence.", d(MORAL_RIGIDITY,6, EMPATHY,3, STRATEGIC_THINKING,1))
        ));

        list.add(q(27, Tier.HARD,
            "You have solid evidence that a powerful, well-liked person in your industry has abused their " +
            "position against several people, none of whom are willing to go on record. Going public yourself " +
            "would likely end your own career even if you're right.",
            attrs(MORAL_RIGIDITY, RISK_TOLERANCE), 13,
            opt("Go public regardless of the cost to yourself.", d(MORAL_RIGIDITY,7, RISK_TOLERANCE,6, POWER_ORIENTATION,3, EMPATHY,3), "A"),
            opt("Stay quiet; without others on record it isn't worth destroying your own life.", d(RISK_TOLERANCE,-5, MORAL_RIGIDITY,-3, EMPATHY,-1), "B"),
            opt("Share the evidence privately with people who can act without your name attached.", d(STRATEGIC_THINKING,8, RISK_TOLERANCE,2, EMPATHY,3)),
            opt("Wait and keep gathering evidence until others are willing to come forward too.", d(STRATEGIC_THINKING,6, RISK_TOLERANCE,-1, MORAL_RIGIDITY,2))
        ));

        list.add(q(28, Tier.HARD,
            "You founded a company that employs 200 people. A buyer offers you a life-changing sum, but you're " +
            "confident they'll gut the culture and likely cut half the jobs within a year. Turning it down means " +
            "years more of grinding, uncertain growth.",
            attrs(MORAL_RIGIDITY, EMPATHY), 18,
            opt("Sell; it isn't your job to guarantee anyone's job forever, including your own past promises.", d(POWER_ORIENTATION,3, RISK_TOLERANCE,3, MORAL_RIGIDITY,-5), "A"),
            opt("Turn it down to protect your people.", d(EMPATHY,7, MORAL_RIGIDITY,4, RISK_TOLERANCE,-2), "B"),
            opt("Negotiate hard for employment protections as a condition of the sale.", d(STRATEGIC_THINKING,8, EMPATHY,3, POWER_ORIENTATION,1)),
            opt("Sell, and personally set aside part of your payout to support anyone who loses their job.", d(POWER_ORIENTATION,2, EMPATHY,5, STRATEGIC_THINKING,3))
        ));

        list.add(q(29, Tier.HARD,
            "You're the on-site coordinator after a disaster with limited rescue resources. Standard triage " +
            "protocol would direct help to those most likely to survive; a family is pleading with you to " +
            "prioritize their trapped relative, whose odds are slim either way.",
            attrs(EMPATHY, MORAL_RIGIDITY), 16,
            opt("Follow protocol strictly, regardless of the pleading.", d(MORAL_RIGIDITY,6, STRATEGIC_THINKING,5, EMPATHY,-4), "A"),
            opt("Divert some resources to the relative because of how much the family is suffering.", d(EMPATHY,8, MORAL_RIGIDITY,-3, STRATEGIC_THINKING,-3), "B"),
            opt("Follow protocol, but personally stay with the family until other help arrives.", d(EMPATHY,5, STRATEGIC_THINKING,3, MORAL_RIGIDITY,2)),
            opt("Let the most senior rescuer on scene override protocol case-by-case.", d(POWER_ORIENTATION,-3, STRATEGIC_THINKING,2, RISK_TOLERANCE,2))
        ));

        list.add(q(30, Tier.HARD,
            "Through a strange turn of events, you are handed near-absolute, temporary authority to redesign one " +
            "major institution exactly as you see fit, with no oversight and no way for anyone to reverse it for " +
            "ten years. You believe you know what's right.",
            attrs(POWER_ORIENTATION, RISK_TOLERANCE, STRATEGIC_THINKING), null,
            opt("Use it fully; a rare chance to fix a broken system shouldn't be wasted on caution.", d(POWER_ORIENTATION,9, RISK_TOLERANCE,6, STRATEGIC_THINKING,2, MORAL_RIGIDITY,-3)),
            opt("Use it, but build in independent oversight and a way for others to reverse your choices.", d(STRATEGIC_THINKING,9, POWER_ORIENTATION,3, MORAL_RIGIDITY,4, EMPATHY,2)),
            opt("Use it only for narrow, reversible fixes, ignoring the bigger changes you'd want to make.", d(MORAL_RIGIDITY,5, RISK_TOLERANCE,-3, STRATEGIC_THINKING,5)),
            opt("Refuse the authority entirely and push for it to be distributed through normal process.", d(POWER_ORIENTATION,-8, MORAL_RIGIDITY,4, RISK_TOLERANCE,-4, STRATEGIC_THINKING,1))
        ));

        if (list.size() != 30) throw new IllegalStateException("Bank must contain exactly 30 questions");
        return Collections.unmodifiableList(list);
    }
}

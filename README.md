SE751-Parallel-Iterator
=======================
The Parallel Iterator allows programmers to safely distribute independent elements for processing to multiple threads. In some cases, the elements may have ordering constraints (for example in the Tree Parallel Iterator). This project involves investigating extensions to the Parallel Iterator concept to allow parallel processing of elements where various ordering constraints (i.e. dependences) might be in place. For example, allowing programmers to specify arbitrary rules to enforce correct processing order, or to extend the Parallel Iterator to allow processing of more complex situations.

Goal: Communicate partial ordering to Parallel Iterator

Steps:
Familiarise ourselves with Parallel Iterator.
Come up with scenarios where we need tasks to produced in a partial order.
Devise a simple way for the programmer to communicate that to Parallel Iterator.

Seminar:
For our seminar we won't have a working solution to present, so we should:
Give an understanding of the problem
Suggest strategies

Seminar tips:
Practice!
1 minute per slide
Don't just repeat what's on the slides
16pt minimum font size
Animations and figures are good (maybe use Prezi?)

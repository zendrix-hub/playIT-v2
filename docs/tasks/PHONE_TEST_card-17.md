# Phone test: card 17 (performance and calm motion) on the Samsung Galaxy A21s

About 10 minutes. Install the debug APK that agy reports after card 17, then:
- Uninstall the old build first, or progress may look odd.
- Use your normal phone settings (font size as you usually have it).

| # | Check | How | Pass if |
|---|---|---|---|
| 1 | No vibration | Tap any big button, a letter card, a picture | The phone never vibrates |
| 2 | Pictures load smoothly | Open Find It for m | The 5 pictures appear without a visible freeze. A picture may fade in a moment later; that's fine |
| 3 | Calm screens | Stay on Hear It for 10 s without touching | The letter card and pictures stand still. Lily moves only when she talks or you tap her |
| 4 | Buttons still react | Tap Play, Next, the mic | Each button presses down and back (that tap animation stays) |
| 5 | Portrait only | Turn the phone sideways on Hear It and on the map | The app stays upright |
| 6 | Map scrolling | Scroll the map up and down quickly for 5 s | Scrolling feels smoother than before (compare with the old build if you still have it) |
| 7 | Sounds still play | Hear It sequence, a correct and a wrong Find It tap | Every sound you heard before still plays |
| 8 | Reduced motion (optional) | Settings > Accessibility > Visibility enhancements > Remove animations: ON, then open Find It and get one wrong | No shaking or floating; the orange highlight still shows |

Tell Claude the results in "run and review", like "1-7 pass, 6 still a bit slow on the map". Map smoothness gets its own fix in card 22, so a slow map now is expected. Note it anyway.
